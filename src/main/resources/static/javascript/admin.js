import * as utils from "./util.js"

$(document).ready(async function () {

    await updateUsersTable()

    $("#submitEditBtn").bind("click", async function() {
        const username = $("#editUsername").val()
        const password = $("#editPassword").val()
        const age = $("#editAge").val()
        if (!username || !password || !age) {
            alert("Пожалуйста, заполните все обязательные поля.");
            return;
        }
        let patchedUser = {
            username: username,
            password: password,
            age: age,
            roles: []
        }
        for (let option of $("#editRoles").children()) {
            if (option.selected) {
                let patchedRoleId = option.value
                await fetch("/roles/" + patchedRoleId)
                    .then(response => response.json())
                    .then(role => {
                        patchedUser.roles.push(role)
                    })
            }
        }
        let patchedUserId = $("#editId").val()
        await fetch("/users/" + patchedUserId, {
            method: "PATCH",
            body: JSON.stringify(patchedUser),
            headers: {
                "Content-Type": "application/json"
            }
        })
        await updateUsersTable()
        if ($("#editId").val() === await utils.getAuthUser()) {
            await utils.updateUserInfo()
        }
        $("#editModal").modal("hide")
    })

    $("#submitDeleteBtn").bind("click", async function() {
        let deletedUserId = $("#deleteId").val()
        if (deletedUserId === await utils.getAuthUser()) {
            window.location = "/logout"
        }
        await fetch("/users/" + deletedUserId, {
            method: "DELETE"
        })
        await updateUsersTable()
        $("#deleteModal").modal("hide")
    })


    $("#submitNewBtn").bind("click", async function() {
        const username = $("#newUsername").val().trim();
        const password = $("#newPassword").val().trim();
        const age = $("#newAge").val().trim();
        if (!username || !password || !age) {
            alert("Пожалуйста, заполните все обязательные поля.");
            return;
        }
        let createdUser = {
            username: username,
            password: password,
            age: age,
            roles: []
        }
        $("#newUsername").val("")
        $("#newPassword").val("")
        $("#newAge").val("")
        for (let option of $("#newRoles").children()) {
            if (option.selected) {
                await fetch("/roles/" + option.value)
                    .then(response => response.json())
                    .then(role => {
                        createdUser.roles.push(role)
                    })
                option.selected = false
            }
        }
        await fetch("/users/addNew", {
            method: "POST",
            body: JSON.stringify(createdUser),
            headers: {
                "Content-Type": "application/json"
            }
        })
        await updateUsersTable()
        window.location = "/admin"
    })
})

async function updateUsersTable() {
    let body = $(".table #allUsers")
    body.empty()
    let users = await fetch("/users")
        .then(response => response.json())
        .then(users => {
            return users
        })
    for (let user of users) {
        let tr = $("<tr/>")
        let th = $("<th/>")

        th.text(user.id)
        tr.append(th)

        let tdUsername = $("<td/>")
        tdUsername.text(user.username)
        tr.append(tdUsername)

        let tdAge = $("<td/>")
        tdAge.text(user.age)
        tr.append(tdAge)

        body.append(tr)
        let tdRoles = $("<td/>")

        let roles = ""
        for (let role of user.roles) {

            roles += `${role.role.substring(5)} `
        }
        tdRoles.text(roles)
        tr.append(tdRoles)

        let tdEdit = $("<td/>")
        let editBtn =$("<button id='editBtn' class='btn btn-primary' type='button'>")
        editBtn.text("Edit")
        editBtn.val(user.id)
        editBtn.bind("click", editFunc)
        tdEdit.append(editBtn)
        tr.append(tdEdit)

        let tdDelete = $("<td/>")
        let deleteBtn =$("<button id='deleteBtn' class='btn btn-danger' type='button'>")
        deleteBtn.text("Delete")
        deleteBtn.val(user.id)
        deleteBtn.bind("click", deleteFunc)
        tdDelete.append(deleteBtn)
        tr.append(tdDelete)
    }
}

async function editFunc() {
    let patchedUser = await fetch("/users/" + this.value)
        .then(response => response.json())
        .then(user => {
            return user
        })
    let select = $("#editRoles")
    select.empty()
    $("#editId").val(patchedUser.id)
    $("#editUsername").val(patchedUser.username)
    $("#editPassword").val(patchedUser.password)
    $("#editAge").val(patchedUser.age)
    let roles = await fetch("/roles")
        .then(response => response.json())
        .then(rolesList => {
            return rolesList
        })
    for (let role of roles) {
        let option = $("<option/>")
        option.val(role.id)
        option.text(role.role)
        for (let userRole of patchedUser.roles) {
            if (role.id === userRole.id) {
                option.attr("selected", true)
                break
            }
        }
        select.append(option)
    }
    $("#editModal").modal("show")
}

async function deleteFunc() {
    let deletedUser = await fetch("/users/" + this.value)
        .then(response => response.json())
        .then(user => {
            return user
        })
    let select = $("#deleteRoles")
    select.empty()
    $("#deleteId").val(deletedUser.id)
    $("#deleteUsername").val(deletedUser.username)
    $("#deletePassword").val(deletedUser.password)
    $("#deleteAge").val(deletedUser.age)
    for (let role of deletedUser.roles) {
        let option = $("<option/>")
        option.text(role.role)
        select.append(option)
    }
    $("#deleteModal").modal("show")
}
