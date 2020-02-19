# Lateral Flow Quantification App
Lateral flow signal quantification app for detecting organ transplantation rejection.

compiled by: Jacqueline Valeri and Miguel A. Alcantar

### Abstract
Infection and rejection are major causes of graft loss in organ transplantation and are linked by the net state of immunosuppression. Refined strategies to monitor transplanted patients are needed to diagnose and treat these conditions earlier in order to improve long-term outcomes.  Here, we show that CRISPR-Cas13 enables fast, low-cost, sensitive point-of-care detection of BK polyomavirus DNA, cytomegalovirus DNA and CXCL9 mRNA, allowing efficient monitoring of common opportunistic viral infections and rejection post-transplantation. BK virus and cytomegalovirus were detected from patient-derived blood and urine samples with high sensitivity and specificity. Similarly, CXCL9 mRNA was detected at elevated levels in urine samples from patients experiencing acute renal transplant rejection. The assay was also adapted for lateral flow read-out, enabling simple visualization and interpretability of results. This work demonstrates the potential for CRISPR-Cas13 diagnostics to facilitate point-of-care post-transplantation monitoring.

### Files
* lat_flow_app/: self-contained source code for the Android App
* ocr/: auxillary functions for openCV
* sample_images/: sample lateral flow assay images to run with the app or notebook
* Lateral_flow_quantifier.ipynb: jupyter notebook with signal quantification workflow (using openCV)
* LICENSE.txt: GPLv3 License
* demo_positive_samples.mov: Supplementary Video File 1 showcasing the app

### Analysis
To utilize the code, download the Jupyter notebook and sample_images folder and run. To utilize and build off the app, clone the repository and build a new project with Android Studio or another IDE. We are currently working on getting an open source license from Chaquopy[https://chaquo.com/chaquopy/license/] so there is a time limit on the app at the moment.

### Demo Video
To reference the app's capability, please see the demo video included with the paper and in this repo.
