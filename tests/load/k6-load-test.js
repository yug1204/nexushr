// k6 Load Test Script for NexusHR
// Day 27: Load testing, security hardening & QA
// Target: 10,000 concurrent virtual users for 15 minutes; P95 < 300ms

import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';
const errorRate = new Rate('errors');
const loginDuration = new Trend('login_duration');
const employeeListDuration = new Trend('employee_list_duration');
const payrollDuration = new Trend('payroll_run_duration');
const aiPredictionDuration = new Trend('ai_prediction_duration');

export const options = {
  stages: [
    { duration: '2m', target: 1000 },    // Ramp up to 1K users
    { duration: '3m', target: 5000 },    // Ramp up to 5K users
    { duration: '5m', target: 10000 },   // Hold at 10K users (peak payroll cycle)
    { duration: '3m', target: 5000 },    // Ramp down
    { duration: '2m', target: 0 },       // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'],    // P95 response time < 300ms
    errors: ['rate<0.01'],               // Error rate < 1%
    login_duration: ['p(95)<500'],
    employee_list_duration: ['p(95)<300'],
  },
};

export default function () {
  const token = login();

  group('Employee Operations', () => {
    listEmployees(token);
    searchEmployees(token);
  });

  group('Attendance', () => {
    getTodayAttendance(token);
  });

  group('Payroll', () => {
    getPayslip(token);
  });

  group('AI Insights', () => {
    getAIDashboard(token);
    getHighRiskEmployees(token);
  });

  sleep(1);
}

function login() {
  const payload = JSON.stringify({
    email: 'admin@nexushr.com',
    password: 'demo1234',
  });

  const res = http.post(`${BASE_URL}/auth/login`, payload, {
    headers: { 'Content-Type': 'application/json' },
  });

  loginDuration.add(res.timings.duration);

  check(res, {
    'login status 200': (r) => r.status === 200,
    'login has token': (r) => r.json('data.accessToken') !== undefined,
  }) || errorRate.add(1);

  return res.json('data.accessToken') || '';
}

function listEmployees(token) {
  const res = http.get(`${BASE_URL}/employees?page=0&size=20`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  employeeListDuration.add(res.timings.duration);
  check(res, { 'employee list 200': (r) => r.status === 200 }) || errorRate.add(1);
}

function searchEmployees(token) {
  const res = http.get(`${BASE_URL}/employees/search?q=Priya`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  check(res, { 'employee search 200': (r) => r.status === 200 }) || errorRate.add(1);
}

function getTodayAttendance(token) {
  const res = http.get(`${BASE_URL}/attendance/today`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  check(res, { 'attendance 200': (r) => r.status === 200 }) || errorRate.add(1);
}

function getPayslip(token) {
  const res = http.get(`${BASE_URL}/payroll/payslip/EMP001?month=5&year=2026`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  payrollDuration.add(res.timings.duration);
  check(res, { 'payslip 200 or 404': (r) => r.status === 200 || r.status === 404 });
}

function getAIDashboard(token) {
  const res = http.get(`${BASE_URL}/ai/dashboard`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  aiPredictionDuration.add(res.timings.duration);
  check(res, { 'ai dashboard 200': (r) => r.status === 200 }) || errorRate.add(1);
}

function getHighRiskEmployees(token) {
  const res = http.get(`${BASE_URL}/ai/attrition/high-risk`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  check(res, { 'high risk 200': (r) => r.status === 200 }) || errorRate.add(1);
}
