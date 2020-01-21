let username = document.getElementById("username");
let email = document.getElementById("email");
let avatar = document.getElementById("avatar");
let createUrl = `http://localhost:8080/collab/users`;

const sumbit = document.getElementById("submit").addEventListener("click", e => {
    e.preventDefault();
    url= 'http://localhost:8080/collab/users';
    let data = {
        "email": email.value,
        "role": "PLAYER",
        "username": username.value,
        "avatar": avatar.value
    };

    fetch(url, {
        method: "POST",
        body: JSON.stringify(data), // data can be `string` or {object}!
        headers: {
            "Content-Type": "application/json"
        },
        mode: "cors"
    })
        .then(function (response) {
            if (!response.ok) {
                alert(
                    "danger",
                    "Something went wrong... The user has not been added to the system"
                );
                throw Error(
                    "The error code: " + response.status + response.statusText
                );
            }
            return response;
        })
        .then(res => res.json())
        .then(function (response) {
            console.log("Success:", JSON.stringify(response));
            window.location.href = "./index.html";
        })
        .catch(error => {
            alert(
                "Danger-Something went wrong... The element has not been added to the system"
            );
            console.log("Error:", error);
        });
});
