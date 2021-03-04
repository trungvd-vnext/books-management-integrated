<template>
  <div>
    <navbar-component></navbar-component>
    <div class="d-flex">
      <div class="content p-4">
        <div class="row mb-4">
          <div class="col-md">
            <div class="d-flex border">
              <div class="flex-grow-1 bg-white p-4">

                <div class="list row">
                  <div class="col-md-8">
                    <div class="input-group mb-3">
                      <input
                        type="text"
                        class="form-control"
                        placeholder="Search by title"
                        v-model="searchTitle"
                      />
                      <div class="input-group-append">
                        <button
                          class="btn btn-outline-secondary"
                          type="button"
                          @click="
                            page = 1;
                            retrieveBooks();
                          "
                        >
                          Search
                        </button>
                      </div>
                    </div>
                  </div>

                  <div class="col-md-12">
                    <div class="mb-3">
                      Items per Page:
                      <select
                        v-model="pageSize"
                        @change="handlePageSizeChange($event)"
                      >
                        <option
                          v-for="size in pageSizes"
                          :key="size"
                          :value="size"
                        >
                          {{ size }}
                        </option>
                      </select>
                    </div>

                    <b-pagination
                      v-model="page"
                      :total-rows="count"
                      :per-page="pageSize"
                      prev-text="Prev"
                      next-text="Next"
                      @change="handlePageChange"
                    ></b-pagination>
                  </div>

                  <div class="col-md-6">
                    <h4>Books List</h4>
                    <ul class="list-group" id="Home">
                      <li
                        class="list-group-item"
                        :class="{ active: index == currentIndex }"
                        v-for="(book, index) in books"
                        :key="index"
                        @click="setActiveBook(book, index)"
                      >
                        {{ book.title }}
                      </li>
                    </ul>

                    <button
                      class="m-3 btn btn-sm btn-danger"
                      @click="removeAllBooks"
                    >
                      Remove All
                    </button>
                  </div>

                  <div class="col-md-6">
                    <div v-if="currentBook">
                      <h4>Book</h4>
                      <div>
                        <label><strong>Title:</strong></label>
                        {{ currentBook.title }}
                      </div>
                      <div>
                        <label><strong>Description:</strong></label>
                        {{ currentBook.description }}
                      </div>
                      <div>
                        <label><strong>Status:</strong></label>
                        {{ currentBook.published ? "Published" : "Pending" }}
                      </div>
                      <div>
                        <img v-bind:src="currentBook.data" width="150px" height="200px" alt="">
                      </div><br/>

                      <router-link
                        v-bind:to="'/books/' + currentBook.id"
                        active-class="active"
                        tag="button"
                        >Edit</router-link
                      >
                    </div>
                    <div v-else>
                      <br />
                      <p>Please click on a Book...</p>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import UserInfoStore from "../app/user-info-store";
import BookDataService from "../services/BookDataService";

export default {
  name: "Home",
  data() {
    return {
      userPoolId: process.env.VUE_APP_COGNITO_USERPOOL_ID,
      userInfo: UserInfoStore.state.cognitoInfo,

      books: [],
      currentBook: null,
      currentIndex: -1,
      searchTitle: "",

      page: 1,
      count: 0,
      pageSize: 15,

      pageSizes: [15, 20, 30],
    };
  },
  methods: {
    getRequestParams(searchTitle, page, pageSize) {
      let params = {};

      if (searchTitle) {
        params["title"] = searchTitle;
      }

      if (page) {
        params["page"] = page - 1;
      }

      if (pageSize) {
        params["size"] = pageSize;
      }

      return params;
    },

    retrieveBooks() {
      const params = this.getRequestParams(
        this.searchTitle,
        this.page,
        this.pageSize
      );

      BookDataService.getAll(params)
        .then((response) => {
          const { books, totalItems } = response.data;
          this.books = books;
          this.count = totalItems;

          console.log(response.data);
        })
        .catch((e) => {
          console.log(e);
        });
    },

    handlePageChange(value) {
      this.page = value;
      this.retrieveBooks();
    },

    handlePageSizeChange(event) {
      this.pageSize = event.target.value;
      this.page = 1;
      this.retrieveBooks();
    },

    refreshList() {
      this.retrieveBooks();
      this.currentBook = null;
      this.currentIndex = -1;
    },

    setActiveBook(book, index) {
      book.data = 'data:image/jpeg;base64,' + book.data;
      this.currentBook = book;
      this.currentIndex = index;
    },

    removeAllBooks() {
      BookDataService.deleteAll()
        .then((response) => {
          console.log(response.data);
          this.refreshList();
        })
        .catch((e) => {
          console.log(e);
        });
    },
  },
  mounted() {
    this.retrieveBooks();
  },
};
</script>

<style>
@import "../css/dashmin.css";
</style>
