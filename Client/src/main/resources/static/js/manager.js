if (localStorage.role !== 'MANAGER') {
	window.location.assign("http://localhost:8080/index.html")
}

const submitItem = document.getElementById("submitItem").addEventListener("click", e => {
  e.preventDefault();
  resetNotifactions();
  let photo = document.getElementById("image-file").files[0];
  console.log(photo);
  console.log(photo.name);
  const itemPictures = "Images/home/" + photo.name;
  var catgorySelection = document.getElementById("categorySelection");
  const itemCategory = catgorySelection.options[catgorySelection.selectedIndex].text + "Type";
  console.log(itemCategory);
  const itemName = document.getElementById("itemName");
  const itemColor = document.getElementById("itemColor");
  var sizeSelection = document.getElementById("sizeSelection");
  const itemSize = sizeSelection.options[sizeSelection.selectedIndex].text;
  const itemDescription = document.getElementById("itemDescription");
  const itemPrice = document.getElementById("itemPrice");
  const managerDomain = localStorage.domain;
  const managerEmail = localStorage.email;
  const url = `http://localhost:8080/collab/elements/${managerDomain}/${managerEmail}`;
  let getActive = document.getElementById("true");
  let isEmpty = checkFieldsItem(itemName, itemColor, itemSize, itemDescription, itemPrice);

  if (getActive.checked) {
    getActive = true;
  } else {
    getActive = false;
  }

  if (!isEmpty) {
    let data = {
      elementId: null,
      type: itemCategory,
      name: itemName.value,
      active: getActive,
      createdBy: {
        userId: {
          email: managerEmail,
          domain: managerDomain
        }
      },
      elementAttributes: {
        color: itemColor.value,
        size: itemSize,
        description: itemDescription.value,
        price: itemPrice.value,
        picture: itemPictures,
      }
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
          document.getElementById("msgItemError").style.display = 'block';
          throw Error("The error code: " + response.status + response.statusText);
        }
        return response;
      })
      .then(res => res.json())
      .then(function (response) {
        document.getElementById("msgItem").style.display = 'block';
        console.log("Success:", JSON.stringify(response));
      })
      .catch(error => {
        document.getElementById("msgItemError").style.display = 'block';
        console.log("Error:", error);
      });
  } else document.getElementById("emptyItem").style.display = "block";
});

function checkFieldsItem(itemName, itemColor, itemSize, itemDescription, itemPrice, itemPictures, parentElementId, parentElementDomain) {
  if (itemName.value === "") return true;
  if (itemColor.value === "") return true;
  if (itemSize.value === "") return true;
  if (itemDescription.value === "") return true;
  if (itemPrice.value === "") return true;
  return false;
}

function resetNotifactions() {
  document.getElementById("emptyItem").style.display = "none";
  document.getElementById("msgItemError").style.display = "none";
  document.getElementById("msgItem").style.display = "none";
}
