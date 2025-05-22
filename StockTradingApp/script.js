const API_URL = "http://localhost:8080/api";

// Register new user
function register() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  fetch(`${API_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
  .then(res => res.text())
  .then(alert)
  .catch(err => alert("Registration failed: " + err.message));
}

// Login existing user
function login() {
  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  fetch(`${API_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  })
  .then(res => res.text())
  .then(result => {
    if (result === "Login successful") {
      // Now fetch user data by username
      fetch(`${API_URL}/users/byUsername/${username}`)
        .then(res => res.json())
        .then(user => {
          console.log("User object:", user);

          // Store userId and username in localStorage
          localStorage.setItem("userId", user.id);
          localStorage.setItem("username", username);

          alert("Login successful!");
          window.location.href = "dashboard.html";
        })
        .catch(err => {
          console.error("Error fetching user by username:", err);
          alert("Failed to fetch user data");
        });
    } else {
      alert("Login failed: " + result);
    }
  })
  .catch(err => alert("Login error: " + err.message));
}
