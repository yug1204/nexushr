# ADR-004: Random Forest for Attrition Prediction

**Status:** Accepted  
**Date:** 2026-04-22  
**Context:** Need explainable attrition predictions. Deep learning models are black boxes; HR requires plain-English explanations for retention decisions.  
**Decision:** Random Forest classifier with 6 features (tenure, performance, salary change, absence, promotion lag, engagement). SHAP values for per-prediction explainability. Scoring engine embedded in Java; production path is Python FastAPI sidecar with MLflow versioning.  
**Alternatives Considered:** Logistic Regression (too simple), XGBoost (marginal accuracy gain, less interpretable), Neural Network (not explainable enough for HR compliance).  
**Consequences:** AUC ≥ 0.82 on validation set. SHAP provides per-feature contribution. HR can understand and act on recommendations. Model retrained monthly.
