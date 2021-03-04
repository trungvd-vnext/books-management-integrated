<template>
  <div>
    <nav class="navbar navbar-expand navbar-dark bg-primary">
      <a class="navbar-brand" href="/">
        <img
          class="navbar-icon d-inline-block align-top"
          src="../../assets/logo.png"
        />
        <span class="navbar-title">Books Management</span>
      </a>
      <div class="navbar-nav mr-auto">
        <li class="nav-item">
          <router-link to="/" class="nav-link">Books</router-link>
        </li>
        <li class="nav-item">
          <router-link to="/add" class="nav-link">Add</router-link>
        </li>
      </div>
      <div class="navbar-collapse collapse">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item dropdown">
            <a
              href="#"
              id="dd_user"
              class="nav-link dropdown-toggle"
              data-toggle="dropdown"
              ><i class="icon fa fa-user"></i> {{ userInfo.username }}</a
            >
            <div
              class="dropdown-menu dropdown-menu-right"
              aria-labelledby="dd_user"
            >
              <router-link to="/logout" class="dropdown-item"
                >Logout</router-link
              >
            </div>
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script>
import UserInfoStore from "../../app/user-info-store";

export default {
  data() {
    return {
      userInfo: UserInfoStore.state.cognitoInfo,
    };
  },
  mounted() {},
};
</script>
