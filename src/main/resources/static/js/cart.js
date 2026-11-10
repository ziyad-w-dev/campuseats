
const cart = document.querySelector(".cart");
const orderId = cart.dataset.orderId;
const itemsList = document.getElementById("cart-items");
const totalElement = document.getElementById("cart-total");
const emptyMessage = document.getElementById("cart-empty");
const errorArea = document.getElementById("cart-error");
const cartHeading = document.getElementById("cart-heading");

function formatMoney(amount) {
  return Number(amount).toFixed(2);
}

function showError(message) {
  errorArea.textContent = message;
}

function clearError() {
  errorArea.textContent = "";
}

async function sendRequest(url, options) {
  let response;
  try {
    response = await fetch(url, options);
  } catch (networkError) {
    throw new Error("Network error, please try again.");
  }

  const body = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(body.message || "Something went wrong (" + response.status + ").");
  }
  return body;
}

function buildRow(item) {
  const row = document.createElement("li");
  row.className = "cart-row";
  row.dataset.orderItemId = item.id;

  const name = document.createElement("span");
  name.className = "cart-row-name";
  name.textContent = item.menuItemName;

  const quantity = document.createElement("span");
  quantity.className = "cart-row-quantity";

  const lineTotal = document.createElement("span");
  lineTotal.className = "cart-row-total";

  const removeButton = document.createElement("button");
  removeButton.type = "button";
  removeButton.className = "remove-button";
  removeButton.dataset.orderItemId = item.id;
  removeButton.setAttribute("aria-label", "Remove " + item.menuItemName + " from cart");
  removeButton.textContent = "Remove";

  row.append(name, quantity, lineTotal, removeButton);
  return row;
}

function renderItem(item) {
  let row = itemsList.querySelector('[data-order-item-id="' + item.id + '"]');
  if (!row) {
    row = buildRow(item);
    itemsList.appendChild(row);
  }
  row.querySelector(".cart-row-quantity").textContent = "× " + item.quantity;
  row.querySelector(".cart-row-total").textContent = formatMoney(item.lineTotal);
}

function renderTotals(cartResponse) {
  totalElement.textContent = formatMoney(cartResponse.cartTotal);
  emptyMessage.hidden = cartResponse.itemCount > 0;
}

function setBusy(button, busyText) {
  button.disabled = true;
  button.setAttribute("aria-busy", "true");
  button.dataset.originalText = button.textContent;
  button.textContent = busyText;
}

function clearBusy(button) {
  button.disabled = false;
  button.removeAttribute("aria-busy");
  button.textContent = button.dataset.originalText;
}

async function addToCart(button) {
  clearError();
  setBusy(button, "Adding...");
  try {
    const cartResponse = await sendRequest("/orders/" + orderId + "/items", {
      method: "POST",
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      body: JSON.stringify({ menuItemId: Number(button.dataset.menuItemId), quantity: 1 }),
    });
    renderItem(cartResponse.item);
    renderTotals(cartResponse);
  } catch (error) {
    showError(error.message);
  } finally {
    clearBusy(button);
  }
}

async function removeFromCart(button) {
  clearError();
  setBusy(button, "Removing...");
  try {
    const cartResponse = await sendRequest(
      "/orders/" + orderId + "/items/" + button.dataset.orderItemId,
      { method: "DELETE", headers: { Accept: "application/json" } }
    );
    button.closest(".cart-row").remove();
    renderTotals(cartResponse);
    cartHeading.focus();
  } catch (error) {
    showError(error.message);
    clearBusy(button);
  }
}

document.addEventListener("click", (event) => {
  const addButton = event.target.closest(".add-button");
  if (addButton) {
    addToCart(addButton);
    return;
  }
  const removeButton = event.target.closest(".remove-button");
  if (removeButton) {
    removeFromCart(removeButton);
  }
});
