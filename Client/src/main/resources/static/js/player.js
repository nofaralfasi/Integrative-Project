if (localStorage.role !== 'PLAYER') {
  window.location.href = ('./index.html');
}

var currentPage;
var type = "cartType";
const userName = localStorage.username;
const userDomain = localStorage.domain;
const userEmail = localStorage.email;

document.getElementById("user_name_headline").innerHTML = "Welcome " + userName + "!";

function onLoad() {
  let i = 1;
  document.getElementById("previousPage").disabled = true;
  currentPage = 1;
  console.log("onLoad()");
  var onLoadUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byNotType/${type}?size=12&page=0`;
  fetch(onLoadUrl, {
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
      responseObejcts.forEach(item => {
        setItemsForPlayer(item, i++);
      });

      itemsURL = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byNotType/${type}?size=12&page=` + currentPage;
      fetch(itemsURL, {
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

          if (responseObejcts.length > 0)
            document.getElementById("nextPage").disabled = false;
          else
            document.getElementById("nextPage").disabled = true
        })
        .catch(error => console.log("Error:", error));
      console.log("Success import items from DB");
    })
    .catch(error => console.log("Error:", error));
}

function setItemsForPlayer(item, i) {
  addItemToPlayersPage(item, i);
  addEventListenerAddToCart("player_item_add_to_cart" + i, item);
}

function addItemToPlayersPage(item, i) {
  var featuredItemsDivs = document.getElementById("featured_items");
  if (featuredItemsDivs == null) return;

  let divCol4Wrapper = document.createElement("div");
  let divImageWrapper = document.createElement("div");
  let divSingleProd = document.createElement("div");
  let divProdInfo = document.createElement("div");

  let imageTag = document.createElement("img");
  let h2TagPrice = document.createElement("h2");
  let pTagName = document.createElement("p");
  let aTagBtnToCart = document.createElement("a");
  let iTagShoppingCart = document.createElement("i");

  divCol4Wrapper.className = "col-sm-4";
  divImageWrapper.className = "product-image-wrapper";
  divSingleProd.className = "single-products";
  divProdInfo.className = "productinfo text-center";

  imageTag.id = "player_item_image" + i;
  imageTag.src = item.elementAttributes.picture;
  imageTag.width = "200";
  imageTag.height = "200";

  h2TagPrice.className = "player_item_price" + i;
  pTagName.className = "player_item_name" + i;
  aTagBtnToCart.className = "btn btn-default add-to-cart";
  iTagShoppingCart.className = "fa fa-shopping-cart";
  h2TagPrice.innerHTML = item.elementAttributes.price;
  pTagName.innerHTML = item.name;
  aTagBtnToCart.appendChild(iTagShoppingCart);
  aTagBtnToCart.innerHTML = "Add to cart";

  divProdInfo.appendChild(imageTag);
  divProdInfo.appendChild(h2TagPrice);
  divProdInfo.appendChild(pTagName);
  divProdInfo.appendChild(aTagBtnToCart);

  let divProdOverlay = document.createElement("div");
  let divOverlayContent = document.createElement("div");
  let h2TagPriceOverlay = document.createElement("h2");
  let pTagNameOverlay = document.createElement("p");
  let aTagBtnToCartOverlay = document.createElement("a");
  let iTagShoppingCartOverlay = document.createElement("i");

  divProdOverlay.className = "product-overlay";
  divOverlayContent.className = "overlay-content";

  h2TagPriceOverlay.className = "player_item_price" + i;
  pTagNameOverlay.className = "player_item_name" + i;
  aTagBtnToCartOverlay.className = "btn btn-default add-to-cart";
  iTagShoppingCartOverlay.className = "fa fa-shopping-cart";
  h2TagPriceOverlay.innerHTML = item.elementAttributes.price;
  pTagNameOverlay.innerHTML = item.name;
  aTagBtnToCartOverlay.appendChild(iTagShoppingCartOverlay);
  aTagBtnToCartOverlay.innerHTML = "Add to cart";
  aTagBtnToCartOverlay.id = "player_item_add_to_cart" + i;

  divOverlayContent.appendChild(h2TagPriceOverlay);
  divOverlayContent.appendChild(pTagNameOverlay);
  divOverlayContent.appendChild(aTagBtnToCartOverlay);
  divProdOverlay.appendChild(divOverlayContent);

  divSingleProd.appendChild(divProdInfo);
  divSingleProd.appendChild(divProdOverlay);
  divImageWrapper.appendChild(divSingleProd);
  divCol4Wrapper.appendChild(divImageWrapper);
  featuredItemsDivs.appendChild(divCol4Wrapper);
}

function removeItemsFromPage() {
  let k = 0;
  let elements = document.getElementsByClassName("col-sm-4");
  let size = elements.length;
  for (k = size - 1; k >= 0; k--) {
    elements[k].remove();
  }
}

function searchItemsByName() {
  removeItemsFromPage();
  let searchName = document.getElementById("searchByName");
  if (searchName.value === "") {
    onLoad();
    return;
  }

  let name = searchName.value;
  let searchURL = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byName/${name}?size=12&page=0`;
  fetch(searchURL, {
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
      let i = 1;
      responseObejcts.forEach(item => {
        setItemsForPlayer(item, i++);
      });
      console.log("Success import Items By name from DB");
    })
    .catch(error => console.log("Error:", error));
}

function searchItemsByCategory(category) {
  removeItemsFromPage();
  let type = category;
  if (type === "showAll") {
    onLoad();
    return;
  }
  let searchByTypeUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byType/${type}?size=12&page=0`;
  fetch(searchByTypeUrl, {
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
      let i = 1;
      responseObejcts.forEach(item => {
        setItemsForPlayer(item, i++);
      });
      console.log("Success import Items By category from DB");
    })
    .catch(error => console.log("Error:", error));
}

function addToCart(elementDomain, elementId, elementIsInCart) {
  console.log(elementIsInCart);
  if (elementIsInCart === true) {
    alert("The item is already inside your cart");
    return;
  }
  let url = "http://localhost:8080/collab/actions";
  let data = {
    "element": {
      "elementId": {
        "domain": elementDomain,
        "id": elementId
      }
    },
    "invokedBy": {
      "userId": {
        "domain": localStorage.domain,
        "email": localStorage.email
      }
    },
    "type": "addToCart",
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
        console.log("danger", "Something went wrong... The element has not been added to the system");
        throw Error("The error code: " + response.status + response.statusText);
      }
      return response;
    })
    .then(function () {
      alert("Item was added to cart successfully!!");
      location.reload();
    })
    .catch(error => {
      alert("Danger-Something went wrong... The element has not been added to the system");
      console.log("Error:", error);
    });
}

function addEventListenerAddToCart(elementToCartBtnId, currItem) {
  let addToCartBtn = document.getElementById("" + elementToCartBtnId);
  let elementDomain = currItem.elementId.domain;
  let elementId = currItem.elementId.id;
  let elementIsInCart = currItem.elementAttributes.inCart;
  addToCartBtn.addEventListener("click", () => addToCart(elementDomain, elementId, elementIsInCart));
}

function goToPreviousPage() {
  document.getElementById("nextPage").disabled = false;
  this.removeItemsFromPage();
  let i = 1;
  currentPage = currentPage - 1;
  document.getElementById("currentPage").innerHTML = currentPage;
  var previousPageUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byNotType/${type}?size=12&page=` + (currentPage-1);
  fetch(previousPageUrl, {
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
      responseObejcts.forEach(item => {
        setItemsForPlayer(item, i++);
      });
      if (currentPage == 1) {
        document.getElementById("previousPage").disabled = true;
        return;
      }
      previousPageUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byNotType/${type}?size=12&page=` + currentPage;
      fetch(previousPageUrl, {
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
          if (responseObejcts.length > 0)
            document.getElementById("previousPage").disabled = false;
          else
            document.getElementById("previousPage").disabled = true;
        })
        .catch(error => console.log("Error:", error));
      console.log("Success import items from DB");
    })
    .catch(error => console.log("Error:", error));
}

function goToNextPage() {
  document.getElementById("previousPage").disabled = false;
  this.removeItemsFromPage();
  let i = 1;
  currentPage = currentPage + 1;
  document.getElementById("currentPage").innerHTML = currentPage;
  let pageUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byNotType/${type}?size=12&page=` + (currentPage-1);
  fetch(pageUrl, {
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
      responseObejcts.forEach(item => {
        setItemsForPlayer(item, i++);
      });
      pageUrl = `http://localhost:8080/collab/elements/${userDomain}/${userEmail}/byNotType/${type}?size=12&page=` + currentPage;
      fetch(pageUrl, {
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
          if (responseObejcts.length > 0)
            document.getElementById("nextPage").disabled = false;
          else
            document.getElementById("nextPage").disabled = true
        })
        .catch(error => console.log("Error:", error));
      console.log("Success import items from DB");
    })
    .catch(error => console.log("Error:", error));
}
