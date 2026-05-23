# NexusHR Attrition Prediction Model Card

## Model Overview
| Field | Value |
|-------|-------|
| **Model Name** | NexusHR Attrition Risk Predictor |
| **Version** | rf-v1.2.0-2026 |
| **Type** | Random Forest (weighted feature scoring) |
| **Task** | Binary classification — predict employee departure within 6 months |
| **Framework** | Java 21 (embedded engine); Python FastAPI sidecar (production path) |
| **Last Updated** | May 2026 |

## Intended Use
- **Primary:** HR teams use attrition risk scores to prioritize retention interventions
- **Secondary:** C-suite workforce planning dashboards
- **Out of Scope:** Not intended for termination decisions, PIP initiation, or any adverse employment actions without human review

## Training Data
| Attribute | Details |
|-----------|---------|
| **Source** | Anonymized HR records from 3 mid-size enterprises (5K-15K employees) |
| **Size** | 28,500 employee records with 6-month outcome labels |
| **Period** | April 2024 – March 2026 |
| **Split** | 70% train / 15% validation / 15% test |
| **Label Balance** | 18% departed / 82% retained (stratified sampling applied) |

## Features (6 input variables)
| Feature | Type | Range | Weight | Rationale |
|---------|------|-------|--------|-----------|
| `tenure_months` | Integer | 0-240 | 18% | Short tenure (<12m) strongest departure predictor |
| `performance_rating` | Float | 1.0-5.0 | 15% | U-shaped: low performers & top performers both at risk |
| `salary_change_pct` | Float | -20 to 50 | 20% | No raise in >18m = highest single risk factor |
| `absence_days` | Integer | 0-60 | 12% | Absenteeism correlates with disengagement |
| `months_since_promotion` | Integer | 0-120 | 15% | >36 months = career stagnation signal |
| `engagement_score` | Float | 0-10 | 20% | Composite from surveys + activity signals |

## Performance Metrics
| Metric | Validation Set | Test Set |
|--------|---------------|----------|
| **AUC-ROC** | 0.84 | 0.82 |
| **Precision** | 0.79 | 0.76 |
| **Recall** | 0.71 | 0.68 |
| **F1 Score** | 0.75 | 0.72 |
| **Accuracy** | 0.83 | 0.81 |

## Explainability
- **Method:** SHAP (SHapley Additive exPlanations)
- **Output:** Per-prediction feature importance values
- **Human-Readable:** Top 3 risk factors in plain English per employee
- **Example:** "1. Salary change of 0.0% (below market adjustment) 2. 42 months since last promotion (career stagnation) 3. Engagement score of 3.5/10 (low engagement alert)"

## Bias Evaluation
| Dimension | Finding | Mitigation |
|-----------|---------|------------|
| Gender | No significant disparity (p > 0.05) | Feature set excludes gender; validated via counterfactual analysis |
| Age | Slight bias toward <25 cohort | Tenure feature normalized; age excluded from input |
| Department | Support dept overrepresented in high-risk | Department-specific thresholds applied |
| Ethnicity | Not collected | N/A — PII minimization by design |

## Limitations
1. Model trained on Indian enterprise data — may not generalize to other geographies
2. Engagement score is self-reported (survey bias possible)
3. Does not account for external market conditions (hiring boom/recession)
4. Prediction horizon is 6 months — not suitable for long-term forecasting
5. Requires minimum 3 months of data per employee for reliable prediction

## Ethical Considerations
- Predictions are advisory only — **never used for automated decisions**
- All predictions are explainable via SHAP — no black-box decisions
- Model retrained monthly to prevent concept drift
- HR team must document reasoning when acting on predictions
- Employee has right to know their risk factors (GDPR Art. 22)

## Monitoring & Retraining
| Aspect | Schedule | Method |
|--------|----------|--------|
| Model Drift | Weekly | KL divergence on feature distributions |
| Performance | Monthly | AUC on recent 30-day outcomes |
| Retraining | Monthly | Full retrain with latest 24-month window |
| Bias Audit | Quarterly | Fairness metrics across protected categories |
