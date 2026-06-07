# AI Model Card: NexusHR Attrition Predictor

## Model Details
* **Developer:** Amdox Technologies
* **Model Date:** April 2026
* **Model Version:** rf-v1.2.0
* **Model Type:** Random Forest Classifier (scikit-learn)
* **License:** Proprietary (Internal Use Only)

## Intended Use
* **Primary Use Case:** Predicting the likelihood of an employee voluntarily leaving the organization within the next 90 days.
* **Secondary Use Case:** Providing actionable HR interventions (e.g., salary adjustment, promotion, training) based on SHAP feature importance.
* **Out-of-Scope:** Using the model to automate termination decisions or penalize employees based on high risk scores.

## Training Data
* **Source:** Historical HR data (2020–2025) across 5 enterprise organizations.
* **Size:** 120,000 employee records (80/20 train-test split).
* **Features:** 
  - Tenure (Months)
  - Performance Rating (1.0 - 5.0)
  - Salary Change Percentage (Last 12 months)
  - Absence Frequency (Days/year)
  - Months Since Last Promotion
  - Engagement Score (Survey & Activity based)
* **Pre-processing:** Synthetic Minority Over-sampling Technique (SMOTE) applied to handle class imbalance (attrition represents only ~12% of the dataset).

## Evaluation Results
* **Accuracy:** 88%
* **AUC-ROC:** 0.84
* **Precision (Attrition Class):** 0.72
* **Recall (Attrition Class):** 0.79
* **F1-Score:** 0.75

## Explainability (SHAP)
The model uses SHAP (SHapley Additive exPlanations) to provide local explainability for every prediction. The top three global drivers for attrition in v1.2.0 are:
1. **Salary Change (20%)** - Stagnant wages strongly correlate with departure.
2. **Engagement Score (20%)** - Sudden drops in intranet activity or survey scores.
3. **Promotion Lag (15%)** - Employees >36 months without role advancement.

## Ethical Considerations & Bias Evaluation
* **Protected Attributes:** The model explicitly excludes Age, Gender, Race, Religion, and Marital Status from the training features to prevent bias.
* **Fairness Testing:** Disparate Impact Ratio was evaluated across Gender (using a proxy historical dataset) and confirmed to be >0.90, indicating no severe algorithmic bias against protected groups.
* **Human-in-the-Loop:** All model outputs require HR Administrator review. The model suggests interventions, but humans must execute them.
