<template>
  <div class="dashboard">
    <header class="header">
      <h1>HRMS Dashboard</h1>
      <div class="user-info">
        <span>{{ user?.name }} ({{ user?.role }})</span>
        <button @click="handleLogout" class="btn-logout">Logout</button>
      </div>
    </header>

    <main class="content">
      <!-- Employee Profile View -->
      <section v-if="user?.role === 'EMPLOYEE'" class="profile-section">
        <h2>My Profile</h2>
        <form @submit.prevent="updateProfile" class="form-container">
          <div class="form-group grid-2">
            <div>
              <label>Name</label>
              <input type="text" v-model="user.name" disabled />
            </div>
            <div>
              <label>Email</label>
              <input type="email" v-model="user.email" disabled />
            </div>
            <div>
              <label>Department</label>
              <input type="text" v-model="user.department" disabled />
            </div>
            <div>
              <label>Joining Date</label>
              <input type="text" v-model="user.joiningDate" disabled />
            </div>
            <div>
              <label>Phone</label>
              <input type="tel" v-model="user.phone" />
            </div>
            <div>
              <label>Address</label>
              <textarea v-model="user.address" rows="3"></textarea>
            </div>
          </div>
          <button type="submit" class="btn-primary" :disabled="loading">Update Profile</button>
          <span v-if="message" class="message success">{{ message }}</span>
        </form>
      </section>

      <!-- HR Management View -->
      <section v-if="user?.role === 'HR'" class="hr-section">
        <div class="hr-header">
          <h2>Employee Management</h2>
          <button @click="showAddForm = !showAddForm" class="btn-primary">
            {{ showAddForm ? 'Cancel Add' : '+ Add Employee' }}
          </button>
        </div>

        <!-- Add Employee Form -->
        <div v-if="showAddForm" class="add-form-card">
          <h3>Add New Employee</h3>
          <form @submit.prevent="addEmployee" class="form-container grid-2">
            <div class="form-group">
              <label>Name</label>
              <input type="text" v-model="newEmployee.name" required />
            </div>
            <div class="form-group">
              <label>Email</label>
              <input type="email" v-model="newEmployee.email" required />
            </div>
            <div class="form-group">
              <label>Role</label>
              <select v-model="newEmployee.role" required>
                <option value="EMPLOYEE">Employee</option>
                <option value="HR">HR</option>
              </select>
            </div>
            <div class="form-group">
              <label>Department</label>
              <input type="text" v-model="newEmployee.department" required />
            </div>
            <div class="form-group">
              <label>Initial Password</label>
              <input type="password" v-model="newEmployee.password" required />
            </div>
            <div class="form-actions full-width">
              <button type="submit" class="btn-primary">Save Employee</button>
            </div>
          </form>
        </div>

        <!-- Employees List -->
        <div class="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Department</th>
                <th>Joining Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="emp in employees" :key="emp.id">
                <td>{{ emp.id }}</td>
                <td>{{ emp.name }}</td>
                <td>{{ emp.email }}</td>
                <td><span class="badge" :class="emp.role.toLowerCase()">{{ emp.role }}</span></td>
                <td>{{ emp.department }}</td>
                <td>{{ emp.joiningDate }}</td>
                <td class="actions">
                  <button @click="deleteEmployee(emp.id)" class="btn-danger btn-sm">Delete</button>
                </td>
              </tr>
              <tr v-if="employees.length === 0">
                <td colspan="7" class="text-center">No employees found.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const user = ref(JSON.parse(localStorage.getItem('user')))
const token = localStorage.getItem('token')

const employees = ref([])
const showAddForm = ref(false)
const loading = ref(false)
const message = ref('')

const newEmployee = ref({
  name: '',
  email: '',
  role: 'EMPLOYEE',
  department: '',
  password: '',
  joiningDate: new Date().toISOString().split('T')[0]
})

// Setup Axios defaults
axios.defaults.baseURL = 'http://localhost:8080/api'
axios.defaults.headers.common['Authorization'] = `Basic ${token}`

onMounted(() => {
  if (!token || !user.value) {
    router.push('/')
    return
  }
  
  if (user.value.role === 'HR') {
    fetchEmployees()
  }
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/')
}

const updateProfile = async () => {
  loading.value = true
  message.value = ''
  try {
    const res = await axios.put('/employees/me', {
      phone: user.value.phone,
      address: user.value.address
    })
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
    message.value = 'Profile updated successfully!'
    setTimeout(() => message.value = '', 3000)
  } catch (err) {
    console.error(err)
    alert('Failed to update profile')
  } finally {
    loading.value = false
  }
}

const fetchEmployees = async () => {
  try {
    const res = await axios.get('/employees')
    employees.value = res.data
  } catch (err) {
    console.error(err)
    if (err.response?.status === 401 || err.response?.status === 403) {
      handleLogout()
    }
  }
}

const addEmployee = async () => {
  try {
    newEmployee.value.joiningDate = new Date().toISOString().split('T')[0]
    await axios.post('/employees', newEmployee.value)
    showAddForm.value = false
    newEmployee.value = { name: '', email: '', role: 'EMPLOYEE', department: '', password: '', joiningDate: '' }
    fetchEmployees()
  } catch (err) {
    console.error(err)
    alert('Failed to add employee')
  }
}

const deleteEmployee = async (id) => {
  if (!confirm('Are you sure you want to delete this employee?')) return
  try {
    await axios.delete(`/employees/${id}`)
    fetchEmployees()
  } catch (err) {
    console.error(err)
    alert('Failed to delete employee')
  }
}
</script>

<style scoped>
.dashboard { min-height: 100vh; background-color: #f9fafb; font-family: sans-serif; }
.header { display: flex; justify-content: space-between; align-items: center; background-color: #ffffff; padding: 1rem 2rem; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.header h1 { margin: 0; font-size: 1.5rem; color: #1f2937; }
.user-info { display: flex; align-items: center; gap: 1rem; color: #4b5563; }
.content { padding: 2rem; max-width: 1200px; margin: 0 auto; }

/* Forms */
.form-container { background: white; padding: 2rem; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
.grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem 2rem; }
.full-width { grid-column: 1 / -1; }
.form-group label { display: block; font-size: 0.875rem; font-weight: 500; color: #374151; margin-bottom: 0.5rem; }
.form-group input, .form-group select, .form-group textarea { width: 100%; padding: 0.5rem; border: 1px solid #d1d5db; border-radius: 4px; color: #1f2937; }
.form-group input:disabled, .form-group textarea:disabled { background-color: #f3f4f6; color: #9ca3af; cursor: not-allowed; }

/* Buttons */
button { cursor: pointer; border: none; border-radius: 4px; padding: 0.5rem 1rem; font-weight: 500; transition: background-color 0.2s; }
.btn-primary { background-color: #2563eb; color: white; }
.btn-primary:hover { background-color: #1d4ed8; }
.btn-primary:disabled { background-color: #93c5fd; cursor: not-allowed; }
.btn-danger { background-color: #ef4444; color: white; }
.btn-danger:hover { background-color: #dc2828; }
.btn-logout { background-color: #e5e7eb; color: #374151; }
.btn-logout:hover { background-color: #d1d5db; }
.btn-sm { padding: 0.25rem 0.5rem; font-size: 0.875rem; }

/* HR Section Specifics */
.hr-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.add-form-card { background-color: #f0fdf4; border: 1px solid #bbf7d0; padding: 1.5rem; border-radius: 8px; margin-bottom: 2rem; }
.add-form-card h3 { margin-top: 0; color: #166534; }
.message { margin-left: 1rem; font-size: 0.875rem; }
.message.success { color: #059669; }

/* Table */
.table-container { background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); overflow-x: auto; }
table { width: 100%; border-collapse: collapse; text-align: left; }
th, td { padding: 1rem; border-bottom: 1px solid #e5e7eb; }
th { background-color: #f9fafb; font-weight: 600; color: #374151; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.05em; }
.text-center { text-align: center; color: #6b7280; }
.badge { display: inline-block; padding: 0.25rem 0.5rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }
.badge.hr { background-color: #dbeafe; color: #1e40af; }
.badge.employee { background-color: #fef3c7; color: #b45309; }
</style>