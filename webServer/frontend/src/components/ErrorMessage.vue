<template>
    <v-flex class="table-size" v-show="errorMessage" wrap>
        <div class="ErrorImage">
            <img class="image" contain src="../assets/mark.png">
        </div>
        <div class="ErrorMessage">
            데이터가 존재하지 않습니다. 다른 옵션을 선택해보세요.
        </div>
    </v-flex>
</template>

<script>
    export default {
        props: ['errorMessage']
    }
</script>
