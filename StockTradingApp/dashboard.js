const API_URL = "http://localhost:8080/api";

// Trade execution (Buy or Sell)
function trade(type) {
  const symbol = document.getElementById("symbol").value;
  const quantity = parseInt(document.getElementById("quantity").value);
  const price = parseFloat(document.getElementById("price").value);
  const userId = localStorage.getItem("userId");

  if (!userId) {
    alert("User not logged in");
    window.location.href = "index.html";
    return;
  }

  const tradeData = {
    userId: Number(userId),
    stockSymbol: symbol,
    quantity: quantity,
    price: price,
    tradeType: type
  };

  const url = type === "BUY" ? `${API_URL}/trades/buy` : `${API_URL}/trades/sell`;

  fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(tradeData)
  })
    .then(res => {
      if (!res.ok) throw new Error("Trade failed");
      return res.json();
    })
    .then(() => {
      alert(`${type} successful!`);
      loadTrades();
      loadPortfolio();
    })
    .catch(err => alert(err.message));
}

// Load trade history
function loadTrades() {
  const userId = localStorage.getItem("userId");
  if (!userId) return;

  fetch(`${API_URL}/trades/user/${userId}`)
    .then(res => res.json())
    .then(data => {
      const list = document.getElementById("history");
      list.innerHTML = "";
      data.forEach(t => {
        const li = document.createElement("li");
        li.textContent = `${t.type}: ${t.quantity} ${t.stockSymbol} @ $${t.price}`;
        list.appendChild(li);
      });
    });
}

// Load portfolio
function loadPortfolio() {
  const userId = localStorage.getItem("userId");
  if (!userId) return;

  fetch(`${API_URL}/trades/portfolio/${userId}`)
    .then(res => res.json())
    .then(data => {
      const container = document.getElementById("portfolio");
      container.innerHTML = "";
      delete data.totalValue;
      for (let symbol in data) {
        const stock = data[symbol];
        container.innerHTML += `<p>${symbol}: ${stock.quantity} shares, Avg: $${stock.averagePrice.toFixed(2)}, Value: $${stock.currentValue.toFixed(2)}</p>`;
      }
    });
}

// Setup on page load
document.addEventListener("DOMContentLoaded", () => {
  const userId = localStorage.getItem("userId");

  if (!userId) {
    alert("User not logged in");
    window.location.href = "index.html";
    return;
  }

  loadTrades();
  loadPortfolio();

  // Attach button click handlers
  const viewHistoryBtn = document.getElementById("viewHistoryBtn");
  const viewPortfolioBtn = document.getElementById("viewPortfolioBtn");

  if (viewHistoryBtn) viewHistoryBtn.addEventListener("click", loadTrades);
  if (viewPortfolioBtn) viewPortfolioBtn.addEventListener("click", loadPortfolio);
});
