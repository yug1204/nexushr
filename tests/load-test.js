import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '2m', target: 2000 }, // Ramp up to 2000 users
    { duration: '5m', target: 10000 }, // Peak load: 10k users during payroll simulation
    { duration: '2m', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'], // 95% of requests must complete below 300ms
    http_req_failed: ['rate<0.01'],   // Error rate must be less than 1%
  },
};

const BASE_URL = 'http://localhost:8080/api/v1';

export default function () {
  // Simulate HR Administrator logging in
  const loginPayload = JSON.stringify({
    email: 'hr.admin@nexushr.local',
    password: 'password123',
  });

  const headers = { 'Content-Type': 'application/json' };

  const loginRes = http.post(`${BASE_URL}/auth/login`, loginPayload, { headers });
  
  check(loginRes, {
    'login successful': (r) => r.status === 200,
    'has token': (r) => r.json('data.token') !== undefined,
  });

  if (loginRes.status === 200) {
    const token = loginRes.json('data.token');
    const authHeaders = {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };

    // Simulate opening the dashboard
    const dashRes = http.get(`${BASE_URL}/employees`, { headers: authHeaders });
    check(dashRes, { 'dashboard loaded': (r) => r.status === 200 });

    sleep(1);

    // Simulate checking a payroll run
    const payrollRes = http.get(`${BASE_URL}/payroll/runs/RUN-54321/payslips`, { headers: authHeaders });
    check(payrollRes, { 'payroll loaded': (r) => r.status === 200 });
  }

  sleep(Math.random() * 3); // Random think time
}
