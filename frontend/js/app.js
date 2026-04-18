const { createApp } = Vue;

const API_BASE = 'http://localhost:8080/api';

createApp({
    data() {
        return {
            user: null, // Current logged-in user details
            authHeaders: null, // Base64 basic auth
            loginForm: { email: '', password: '' },
            employees: [],
            showAddForm: false,
            newEmployee: { name: '', email: '', role: 'EMPLOYEE', department: '', password: '' }
        };
    },
    methods: {
        async login() {
            const token = btoa(`${this.loginForm.email}:${this.loginForm.password}`);
            const headers = { 'Authorization': `Basic ${token}`, 'Content-Type': 'application/json' };
            try {
                // To authenticate, simply request the "me" endpoint
                const res = await fetch(`${API_BASE}/employees/me`, { headers });
                if (res.ok) {
                    this.user = await res.json();
                    this.authHeaders = headers;
                    if (this.user.role === 'HR') {
                        this.fetchEmployees();
                    }
                } else {
                    alert('Login failed. Check credentials.');
                }
            } catch (err) {
                console.error(err);
                alert('Connection error');
            }
        },
        logout() {
            this.user = null;
            this.authHeaders = null;
            this.loginForm = { email: '', password: '' };
        },
        async updateMyProfile() {
            try {
                const res = await fetch(`${API_BASE}/employees/me`, {
                    method: 'PUT',
                    headers: this.authHeaders,
                    body: JSON.stringify({ phone: this.user.phone, address: this.user.address })
                });
                if (res.ok) {
                    alert('Profile updated');
                }
            } catch (e) { console.error(e) }
        },
        async fetchEmployees() {
            try {
                const res = await fetch(`${API_BASE}/employees`, { headers: this.authHeaders });
                if (res.ok) {
                    this.employees = await res.json();
                }
            } catch (e) { console.error(e) }
        },
        async addEmployee() {
            this.newEmployee.joiningDate = new Date().toISOString().split('T')[0];
            try {
                const res = await fetch(`${API_BASE}/employees`, {
                    method: 'POST',
                    headers: this.authHeaders,
                    body: JSON.stringify(this.newEmployee)
                });
                if (res.ok) {
                    alert('Employee added');
                    this.fetchEmployees();
                    this.showAddForm = false;
                    this.newEmployee = { name: '', email: '', role: 'EMPLOYEE', department: '', password: '' };
                }
            } catch (e) { console.error(e) }
        },
        async deleteEmployee(id) {
            if (!confirm('Are you sure?')) return;
            try {
                const res = await fetch(`${API_BASE}/employees/${id}`, {
                    method: 'DELETE',
                    headers: this.authHeaders
                });
                if (res.ok) {
                    this.fetchEmployees();
                }
            } catch (e) { console.error(e) }
        }
    }
}).mount('#app');