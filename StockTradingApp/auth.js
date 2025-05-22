const BASE_URL = 'http://localhost:8080/api/auth'; // Spring Boot backend base URL

document.getElementById('registerForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const username = document.getElementById('regUsername').value;
  const password = document.getElementById('regPassword').value;

  try {
    const response = await fetch(`${BASE_URL}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }) // Must match Spring Boot DTO
    });

    const result = await response.text();
    document.getElementById('message').innerText = result;

    if (response.ok) {
      alert('Registration successful!');
    } else {
      alert('Registration failed: ' + result);
    }
  } catch (error) {
    alert('Error: ' + error);
  }
});

document.getElementById('loginForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const username = document.getElementById('loginUsername').value;
  const password = document.getElementById('loginPassword').value;

  try {
    const response = await fetch(`${BASE_URL}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }) // Must match backend login DTO
    });

    const result = await response.text();
    document.getElementById('message').innerText = result;

    if (response.ok && result === "Login successful") {
      localStorage.setItem("username", username);
      alert("Login successful!");
      window.location.href = "dashboard.html";
    } else {
      alert("Login failed: " + result);
    }
  } catch (error) {
    alert("Error: " + error);
  }
});
