<template>
  <div>
    <navbar-component></navbar-component>
    <div class="d-flex">
      <div class="content p-4">
        <div class="row mb-4">
          <div class="col-md">
            <div class="d-flex border">
              <div class="flex-grow-1 bg-white p-4">
                <div class="submit-form">
                  <div v-if="!submitted">
                    <div class="form-group">
                      <label for="title">Title</label>
                      <input
                        type="text"
                        class="form-control"
                        id="title"
                        required
                        v-model="book.title"
                        name="title"
                      />
                    </div>

                    <div class="form-group">
                      <label for="author">Author</label>
                      <input
                        class="form-control"
                        id="author"
                        required
                        v-model="book.author"
                        name="author"
                      />
                    </div>

                    <div class="form-group">
                      <label for="description">Description</label>
                      <input
                        class="form-control"
                        id="description"
                        required
                        v-model="book.description"
                        name="description"
                      />
                    </div>

                    <div class="form-group">
                      <label>Select an image</label>
                      <div v-if="!book.data">
                        <input
                          type="file" accept="image/*"
                          @change="onFileChange(book, $event)"
                        />
                      </div>
                      <div v-else>
                        <img :src="book.data" />
                        <button @click="removeImage(book)">Remove image</button>
                      </div>
                    </div>

                    <button @click="saveBook" class="btn btn-success">
                      Submit
                    </button>
                  </div>

                  <div v-else>
                    <h4>You submitted successfully!</h4>
                    <button class="btn btn-success" @click="newBook">
                      Add
                    </button>
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
import BookDataService from "../services/BookDataService";

export default {
  name: "add-book",
  data() {
    return {
      book: {
        id: null,
        title: "",
        author: "",
        description: "",
        published: false,
        data: false,
      },
      submitted: false,
    };
  },
  methods: {
    onFileChange(book, e) {
      var files = e.target.files || e.dataTransfer.files;
      if (!files.length) return;
      this.createImage(book, files[0]);
    },
    createImage(book, file) {
      var reader = new FileReader();

      reader.onload = (e) => {
        book.data = e.target.result;
      };
      reader.readAsDataURL(file);
    },
    removeImage: function (book) {
      book.data = false;
    },

    saveBook() {
      var data = {
        title: this.book.title,
        author: this.book.author,
        description: this.book.description,
        data: this.book.data
      };

      BookDataService.create(data)
        .then((response) => {
          this.book.id = response.data.id;
          console.log(response.data);
          this.submitted = true;
        })
        .catch((e) => {
          console.log(e);
        });
    },

    newBook() {
      this.submitted = false;
      this.book = {};
    },
  },
};

</script>

<style>
.submit-form {
  max-width: 300px;
  margin: auto;
}
img {
  width: 30%;
  margin: auto;
  display: block;
  margin-bottom: 10px;
}
</style>
