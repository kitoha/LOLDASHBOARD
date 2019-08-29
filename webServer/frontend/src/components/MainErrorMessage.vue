<template>
    <v-flex class="table-size" wrap>
        <div class="ErrorImage">
            <img class="image" contain src="../assets/mark.png">
        </div>
        <div class="ErrorMessage">
            {{errorMessage}}
        </div>
    </v-flex>
</template>

<script>
    export default {
        props: ['errorMessage']
    }
</script>
