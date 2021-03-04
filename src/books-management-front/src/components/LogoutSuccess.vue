<template>
<div class="row">
    <div class="col">
        <!-- <h2>Logged Out successfully</h2> -->
        <h2>Logging out ...</h2>
        <!-- <router-link to="/login">Login</router-link> -->
    </div>
</div>


</template>
<script>
export default {
    mounted: function(){
        this.$router.push('/');
    }
}
</script>