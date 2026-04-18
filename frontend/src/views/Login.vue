<template>
  <div class="login-container">
    <div class="card">
      <h2>HRMS Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Email</label>
          <input type="email" v-model="email" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input type="password" v-model="password" required />
        </div>
        <button type="submit" :disabled="loading">Login</button>
        <p v-if="error" class="error">{{ error }}</p>
      </form>
      <div class="demo-creds">
        <small>Demo HR: hr@test.com / pass123<br/>Demo Employee: emp@test.com / pass123</small>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const email = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = btoa(`${email.value}:${password.value}`)
    // We hit the /me endpoint to verify credentials
    const response = await axios.get('http://localhost:8080/api/employees/me', {
      headers: {
        Authorization: `Basic ${token}`
      }
    })
    
    // Store user data and token in localStorage
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(response.data))
    
    router.push('/dashboard')
  } catch (err) {
    console.error(err)
    error.value = 'Invalid email or password'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}
.card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}
h2 { margin-top: 0; text-align: center; color: #333; }
.form-group { margin-bottom: 1rem; }
label { display: block; margin-bottom: 0.5rem; color: #666; }
input { width: 100%; padding: 0.75rem; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
button { width: 100%; padding: 0.75rem; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 1rem; margin-top: 1rem; }
button:hover { background-color: #45a049; }
button:disabled { background-color: #cccccc; cursor: not-allowed; }
.error { color: #f44336; text-align: center; margin-top: 1rem; margin-bottom: 0; }
.demo-creds { margin-top: 20px; text-align: center; color: #888; }
</style>