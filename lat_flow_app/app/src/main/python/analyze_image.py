#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Nov 4 10:37:04 2019
@author: Jacqueline Valeri and Miguel A. Alcantar
"""
# importing required packages

import cv2 # openCV -- contains useful functions for image analysis and shape detection
import numpy as np
import skimage # essentially scikit-learn image processing functions
import imutils # useful openCV helper functions
from imutils import contours

def main(arg, virus):

    # read image using openCV
    #Converted the datatype to np.uint8
    pathogen_to_detect = virus
    image_orig = np.asarray(arg, dtype = 'uint8')

    # convert image from BGR to RGB to 8-bit Grayscale
    # Follows same preprocessing that Michael Kaminski uses
    # could probably go BGR2GRAY -- kept it this way just to be safe
    #image_RBG = cv2.cvtColor(image_orig, cv2.COLOR_BGR2RGB)
    image_RBG = image_orig
    image_GRAY = cv2.cvtColor(image_RBG, cv2.COLOR_RGB2GRAY)
    # invert image colors -- again, following what Prof. Kaminski does
    # during signal quantification
    image_invert = cv2.bitwise_not(image_GRAY)
    # saving original inverted image such that contour box is not saved over
    image_invert_orig = image_invert.copy()

    image_blur = cv2.GaussianBlur(image_invert, (11, 11), 0)
    # apply threshold
    thresh = float(cv2.meanStdDev(image_blur)[0]) + 0.6*float(cv2.meanStdDev(image_blur)[1]) # new threshold as of 1/7/2020
    #thresh = float(cv2.meanStdDev(image_blur)[0]) + 0.5*float(cv2.meanStdDev(image_blur)[1])
    image_thresh = cv2.threshold(image_blur, thresh, 255, cv2.THRESH_BINARY)[1]

    # Noise reduction by eroding and dialating
    # page 127 - 129(http://szeliski.org/Book/drafts/SzeliskiBook_20100903_draft.pdf)
    # https://docs.opencv.org/2.4/doc/tutorials/imgproc/erosion_dilatation/erosion_dilatation.html
    image_erode = cv2.erode(image_thresh, None, iterations=1)
    image_no_noise = cv2.dilate(image_erode, None, iterations=1)

    # perform a connected component analysis on the thresholded image
    # essentially, this helps identify the "largest blobs"
    # https://www.pyimagesearch.com/2016/10/31/detecting-multiple-bright-spots-in-an-image-with-python-and-opencv/

    labels = skimage.measure.label(image_no_noise, neighbors=8, background=0)
    mask = np.zeros(image_no_noise.shape, dtype="uint8")

    # loop over the unique components
    for label in np.unique(labels):
        # if this is the background label, ignore it
        if label == 0:
            continue

        # otherwise, construct the label mask and count the
        # number of pixels
        labelMask = np.zeros(image_no_noise.shape, dtype="uint8")
        labelMask[labels == label] = 255
        numPixels = cv2.countNonZero(labelMask)

        # if the number of pixels in the component is sufficiently
        # large, then add it to our mask of "large blobs"
        # only add to mask if the identified sample and/or control band is sufficiently large 
        # sufficiently large here means that the band contains at the very least 3*image_width number of pixels 
        if numPixels > mask.shape[1]*3: # new threshold as of 1/7/2020
        # if numPixels > 300:
            mask = cv2.add(mask, labelMask)
            # find the contours in the mask, then sort them from bottom(control)-to-top(sample)

    cnts = cv2.findContours(mask.copy(), cv2.RETR_EXTERNAL,
        cv2.CHAIN_APPROX_SIMPLE)
    cnts = imutils.grab_contours(cnts)
    cnts = contours.sort_contours(cnts, method="bottom-to-top")[0]

    roi = []
    # loop over the contours

    if len(cnts) > 1:
        for (i, c) in enumerate(cnts):
            # draw the bright spot on the image
            # get the min area rect
            x, y, width, height = cv2.boundingRect(c)
            roi.append(image_invert_orig[y:y+height, x:x+width])
            rect = cv2.minAreaRect(c)
            box = cv2.boxPoints(rect)
            # convert all coordinates floating point values to int
            box = np.int0(box)
            # draw a red 'nghien' rectangle
            cv2.drawContours(image_invert, [box], 0, (0, 0, 255), thickness=2)

    elif len(cnts) == 1:
        # draw the bright spot on the image
        # get the min area rect
        for (i, c) in enumerate(cnts):
            x, y, width, height = cv2.boundingRect(c)
            roi.append(image_invert_orig[y:y+height, x:x+width])
            rect = cv2.minAreaRect(c)
            box = cv2.boxPoints(rect)
            # convert all coordinates floating point values to int
            box = np.int0(box)
            # draw a red 'nghien' rectangle
            #cv2.drawContours(image_invert, [box], 0, (0, 0, 255), thickness=2) # old as of 1/7/2020
            cv2.drawContours(image_orig, [box], 0, (0, 0, 255), thickness=2)


        # estimate location of sample band
        # we essentially take the control band contours, and drag it along two locations
        # near the top of the strip, where the sample band is likely to be located
        box_ctl_roi = np.array([[0,height], [0,0], [width,0], [width,height]])
        roi_test_1 = image_invert_orig[height:2*height, 0:width]
        roi_test_2 = image_invert_orig[2*height:3*height, 0:width]
        
        if cv2.meanStdDev(roi_test_1)[0] > cv2.meanStdDev(roi_test_2)[0]:
            box_ctl_roi[:,1] = box_ctl_roi[:,1]+height
            roi.append(roi_test_1)
        else:
            box_ctl_roi[:,1] = box_ctl_roi[:,1]+2*height
            roi.append(roi_test_2)
        ## OLD AS OF 1/7/2020 ##
        #if pathogen_to_detect == 'CMV':
        #    roi_shift = np.int0(round(width*4.345))
        #else:
        #    roi_shift = np.int0(round(width*3.8))
        #roi.append(image_invert_orig[y-roi_shift:y+height-roi_shift, x:x+width])
        #box[:,1] = box[:,1]-roi_shift
        ## OLD AS OF 1/7/2020 ##
        cv2.drawContours(image_invert, [box], 0, (0, 0, 255), thickness=2)

    else:
        result_string = "ERROR. No bands located. Please retake photo."
        return(result_string)

    ratio = float(cv2.meanStdDev(roi[1])[0] / cv2.meanStdDev(roi[0])[0])


    ratio_string = "The calculated sample-to-control ratio is " +str(ratio)
    thresh = 0.50 # changed from 0.4 on 1/7/2020
    if ratio > thresh:
        result_string = "The sample band ratio " + str(np.round(ratio,2)) + " suggests a positive test result for " + pathogen_to_detect + "."
    else:
        result_string = "The sample band ratio " + str(np.round(ratio,2)) + " suggests a negative test result for " + pathogen_to_detect + "."

        print(ratio_string)
    return(result_string)
