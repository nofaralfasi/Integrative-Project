let tbodyContainer = document.getElementById("tbody-container")
const userDomain = localStorage.domain;
const userEmail = localStorage.email;
const nameOfUser = userDomain + "@@" + userEmail;
var cartElement;
let cartUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byName/${nameOfUser}`;

fetch(cartUrl, {
  method: "GET",
  headers: {
    "Content-Type": "application/json"
  },
  mode: "cors"
})
  .then(function (response) {
    if (!response.ok) {
      console.log("danger-Something went wrong...");
      throw Error("The error code: " + response.status + response.statusText);
    }
    return response;
  })
  .then(res => res.json())
  .then(function (responseObjects) {
    console.log(responseObjects);
    if (responseObjects == null)
      alert("You didn't add anything to your cart!")
    else {
      responseObjects.forEach(item => {
        cartElement = item;
        document.getElementById("cart_total_price").innerText = item.elementAttributes.totalPrice;
        document.getElementById("tax_price").innerText = "$2";
        document.getElementById("cart_total_price_after_tax").innerText = "$" + (item.elementAttributes.totalPrice + 2);
        const cartItemsURL = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byParent/${userDomain}/${item.elementId.id}?size=12&page=0`;
        fetch(cartItemsURL, {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          },
          mode: "cors"
        })
          .then(function (response) {
            if (!response.ok) {
              console.log("danger-Something went wrong...");
              throw Error("The error code: " + response.status + response.statusText);
            }
            return response;
          })
          .then(res => res.json())
          .then(function (responseObejcts) {
            console.log(responseObejcts);
            let i = 1;
            responseObejcts.forEach(item => {
              addItemToCart(item, i++);
            });
            console.log("Success import items from DB for Cart");
          })
          .catch(error => console.log("Error:", error));
      });
    }
  })
  .catch(error => console.log("Error:", error));

function addItemToCart(item, i) {
  let trWrapper = document.createElement("tr");

  let tdProductWrapper = document.createElement("td");
  let aImageTag = document.createElement("a");
  let imageTag = document.createElement("img");
  tdProductWrapper.className = "cart_product";
  imageTag.id = "cart_item_image" + i;
  imageTag.src = item.elementAttributes.picture;
  aImageTag.appendChild(imageTag);
  tdProductWrapper.appendChild(aImageTag);

  let tdDescWrapper = document.createElement("td");
  let h4DescTagWrapper = document.createElement("h4");
  let aH4Child = document.createElement("a");
  tdDescWrapper.className = "cart_description";
  h4DescTagWrapper.id = "cart_item_description" + i;
  aH4Child.innerText = item.name;
  h4DescTagWrapper.appendChild(aH4Child);
  tdDescWrapper.appendChild(h4DescTagWrapper);

  let tdColorWrapper = document.createElement("td");
  let pColorTag = document.createElement("p");
  tdColorWrapper.className = "cart_item_color";
  pColorTag.id = "color" + i;
  pColorTag.innerText = item.elementAttributes.color;
  tdColorWrapper.appendChild(pColorTag);

  let tdPriceWrapper = document.createElement("td");
  let pTotalPriceTag = document.createElement("p");
  tdPriceWrapper.className = "cart_total";
  pTotalPriceTag.className = "cart_total_price";
  pTotalPriceTag.id = "price" + i;
  pTotalPriceTag.innerText = item.elementAttributes.price;
  tdPriceWrapper.appendChild(pTotalPriceTag);

  let tdDeleteWrapper = document.createElement("td");
  let aDeleteTag = document.createElement("a");
  let iDeleteTag = document.createElement("i");
  tdDeleteWrapper.className = "cart_delete";
  aDeleteTag.className = "cart_quantity_delete";
  iDeleteTag.className = "fa fa-times";
  iDeleteTag.id = "delete_cart_item" + i;

  iDeleteTag.addEventListener("click", () => removeFromCart(item));
  aDeleteTag.appendChild(iDeleteTag);
  tdDeleteWrapper.appendChild(aDeleteTag);
  trWrapper.appendChild(tdProductWrapper);
  trWrapper.appendChild(tdDescWrapper);
  trWrapper.appendChild(tdColorWrapper);
  trWrapper.appendChild(tdPriceWrapper);
  trWrapper.appendChild(tdDeleteWrapper);
  tbodyContainer.appendChild(trWrapper);
}

function removeFromCart(item) {
  let url = "http://localhost:8080/collab/actions";
  let data = {
    "element": {
      "elementId": {
        "domain": item.elementId.domain,
        "id": item.elementId.id
      }
    },
    "invokedBy": {
      "userId": {
        "domain": userDomain,
        "email": userEmail
      }
    },
    "type": "removeFromCart",
    "actionAttributes": null
  };
  fetch(url, {
    method: "POST",
    body: JSON.stringify(data),
    headers: {
      "Content-Type": "application/json"
    },
    mode: "cors"
  })
    .then(function (response) {
      if (!response.ok) {
        alert("danger", "Something went wrong... The element has not been added to the system");
        throw Error("The error code: " + response.status + response.statusText);
      }
      return response;
    })
    .then(function () {
      alert("Item was removed from cart successfully");
      location.reload();
    })
    .catch(error => {
      alert("Danger-Something went wrong..");
      console.log("Error:", error);
    });
}

function buyAction() {
  let url = "http://localhost:8080/collab/actions";
  if (cartElement.elementAttributes.totalPrice === 0) {
    alert("Nothing to buy in the cart");
    return;
  }
  let data = {
    type: "buy",
    invokedBy: {
      userId: {
        domain: userDomain,
        email: userEmail
      }
    },
    element: {
      elementId: {
        domain: cartElement.elementId.domain,
        id: cartElement.elementId.id
      }
    },
    actionAttributes: null
  };
  console.log(data);
  fetch(url, {
    method: "POST",
    body: JSON.stringify(data),
    headers: {
      "Content-Type": "application/json"
    },
    mode: "cors"
  })
    .then(function (response) {
      console.log(response);
      if (response.status !== 200) {
        alert("danger", "Something went wrong... The buying proccss was terminated");
        throw Error("The error code: " + response.status + response.statusText);
      }
      if (response.json() == null) {
        alert("danger", "Something went wrong... You need to buy all items on cart!");
      }
      return response;
    })
    .then(function () {
      alert("You have bought all items on your cart!" + "check your email for confirmation");
      location.reload();
    })
    .catch(error => {
      alert("Danger-Something went wrong... The element has not been added to the system");
      console.log("Error:", error);
    });
  //document.getElementById("buy").disabled = true;
}
