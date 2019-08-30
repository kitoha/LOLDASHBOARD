<template>
    <v-overlay color="white" v-show="loading">
        <v-progress-circular
                :size="150"
                color="primary"
                indeterminate
                width="10"
        >데이터받는중...
        </v-progress-circular>
    </v-overlay>
</template>

<script>
    export default {
        props: ['loading']
    }
</script>
