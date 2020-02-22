# Lateral Flow Quantification App
Computational platform and app for quantifying band intensities from diagnostic lateral flow strips. The code presented in this repo accompany the following manuscript:


> Michael M. Kaminski, Miguel A. Alcantar, Isadora Lape, Robert Greensmith, Allison C. Huske,Jacqueline A. Valeri, Francisco M. Marty, Verena Klämbt, Jamil Azzi, Enver Akalin, Leonardo V. Riella, and James J. Collins. Towards CRISPR-based diagnostics for transplantation medicine. *In review*


Repo was compiled by: Jacqueline A. Valeri and Miguel A. Alcantar. 

## Abstract
Infection and rejection are major causes of graft loss in organ transplantation and are linked by the net state of immunosuppression. Refined strategies to monitor transplanted patients are needed to diagnose and treat these conditions earlier in order to improve long-term outcomes.  Here, we show that CRISPR-Cas13 enables fast, low-cost, sensitive point-of-care detection of BK polyomavirus DNA, cytomegalovirus DNA and CXCL9 mRNA, allowing efficient monitoring of common opportunistic viral infections and rejection post-transplantation. BK virus and cytomegalovirus were detected from patient-derived blood and urine samples with high sensitivity and specificity. Similarly, CXCL9 mRNA was detected at elevated levels in urine samples from patients experiencing acute renal transplant rejection. The assay was also adapted for lateral flow read-out, enabling simple visualization and interpretability of results. This work demonstrates the potential for CRISPR-Cas13 diagnostics to facilitate point-of-care post-transplantation monitoring.

## Installation

~~~~~~~~~~~~~~~
git clone https://github.com/jackievaleri/lateral_flow_quantification_app.git
~~~~~~~~~~~~~~~

Android app code can be compiled using Android Studio (Google, Mountain View, CA).

## Directory structure
* `lat_flow_app/`: self-contained source code for the Android App
* `ocr/`: auxillary functions for image processing 
* `sample_images/`: sample lateral flow assay images to run with the app or jupyter notebook
* `Lateral_flow_quantifier.ipynb`: jupyter notebook with signal quantification workflow (using openCV)
* `LICENSE.txt`: GPLv3 License
* `demo_positive_samples.mov`: Supplementary Video File 1 showcasing the app

## Analysis
To utilize the code, download the Jupyter notebook and sample_images folder and run. To utilize and build off the app, clone the repository (see installation) and build a new project with Android Studio or another android IDE. We are currently working on getting an open source license from [Chaquopy](https://chaquo.com/chaquopy/license/) so there is a time limit on the app at the moment.

## Demo Video
To reference the app's capability, please see the demo video included with the paper and in this repo: demo_positive_samples.mov
