const submit = document.getElementById("submit").addEventListener("click", e => {
    e.preventDefault();
    resetNotifactions();
    let email = document.getElementById("InputEmail");
    let domain = document.getElementById("domainSelect");
    let domainValue = domain.value;

    let url = "http://localhost:8080/collab/users/login/" + domainValue + "/" + email.value;
    if (email.value === "") {
      document.getElementById("alertMsgEmptyMail").style.display = "block";
      document.getElementById("email-feedback").style.display = "block";
    } else {
      if (!checkMail(document.getElementById("InputEmail")))
        document.getElementById("alertMsg").style.display = "block";
      else {
        fetch(url, {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          },
          mode: "cors"
        })
          .then(res => res.json())
          .then(function (responseObejcts) {
            console.log("Logged Output-: responseObejcts", responseObejcts.status);
            console.log(responseObejcts.status)
            if (!(responseObejcts.status === undefined))
              document.getElementById("alertMsgNet").style.display = "block";
            else {
              console.log("Success:", responseObejcts);
              localStorage.setItem("role", responseObejcts.role);
              localStorage.setItem("domain", responseObejcts.userId.domain);
              localStorage.setItem("email", responseObejcts.userId.email);
              localStorage.setItem("username", responseObejcts.username);
              role = localStorage.role;
              if (role === "undefined") localStorage.role = "";
              else if (role === "MANAGER")
                window.location.href = "./manager.html";
              else if (role === "PLAYER")
                window.location.href = "./player.html";
            }
          })
          .catch(error => {
            console.log("Error:", error);
            document.getElementById("alertMsgNet").style.display = "block";
          });
      }
    }
  });

function checkMail(email) {
  const pattern = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return pattern.test(String(email.value).toLowerCase()) ? true : false;
}

function resetNotifactions() {
  document.getElementById("alertMsg").style.display = "none";
  document.getElementById("alertMsgNet").style.display = "none";
  document.getElementById("alertMsgEmptyMail").style.display = "none";
  document.getElementById("InputEmail").classList.toggle("is-invalid");
}

function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();
  console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
  console.log('Name: ' + profile.getName());
  console.log('Image URL: ' + profile.getImageUrl());
  console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
}
