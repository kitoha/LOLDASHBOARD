<template>
    <v-flex>
        <table class="table-size">
            <thead>
            <tr>
                <th>그래프 종류</th>
                <th>시간</th>
                <th>게임 종류</th>
            </tr>
            </thead>
            <tbody bgcolor="white">
            <tr>
                <td>
                    <v-radio-group class="ml-5" v-model="graphBtnStatus">
                        <v-radio class="value-font" label="승률" value="winning-rate" v-on:click="getWinRateData"></v-radio>
                        <v-radio class="value-font" label="밴 픽률" v-on:click="getBannedData"
                                 value="ban-pick"></v-radio>
                        <v-radio class="value-font" label="게임당 픽률" v-on:click=" getChampionData"
                                 value="game-pick"></v-radio>
                    </v-radio-group>
                </td>
                <td>
                    <v-radio-group class="ml-5" v-model="timeBtnStatus">
                        <v-radio class="value-font" label="최근 7일" v-on:click="getWeekData"
                                 value="weeks"></v-radio>
                        <v-radio class="value-font" label="최근 1일" v-on:click="getDayData" value="days"></v-radio>
                        <v-radio class="value-font" label="한시간" v-on:click="getHourData" value="hour"></v-radio>
                    </v-radio-group>
                </td>

                <td>
                    <v-radio-group class="ml-5" v-model="modeBtnStatus">
                        <v-radio class="value-font" label="랭크게임" value="rank"></v-radio>
                        <v-radio class="value-font" label="노말" value="normal"></v-radio>
                    </v-radio-group>
                </td>
            </tr>
            </tbody>
        </table>
    </v-flex>
</template>

<script>
    export default {
        data() {
            return {
                tableData: {},
                graphBtnStatus: "game-pick",
                timeBtnStatus: "hour",
                modeBtnStatus: "rank",
                gamemode: "SoloRank",
                gamemodeText: "게임당 픽률",
                timemode: "Hour",
            }
        },
        created() {
            this.tableData.gamemode = "SoloRank"
            this.tableData.timemode = "Hour"
            this.tableData.gamemodeText = "게임당 픽률"
        },
        methods: {
            getBannedData: function () {
                this.graphBtnStatus = "ban-pick"
                this.tableData.gamemode = "BAN"
                this.tableData.gamemodeText = "게임당 밴률"
                this.$emit("UpperTable", this.tableData)
            },
            getChampionData: function () {
                this.graphBtnStatus = "game-pick"
                this.tableData.gamemode = "SoloRank"
                this.tableData.gamemodeText = "게임당 픽률"
                this.$emit("UpperTable", this.tableData)
            },
            getDayData: function () {
                this.timeBtnStatus = "days"
                this.tableData.timemode = "Day"
                this.$emit("UpperTable", this.tableData)
            },
            getHourData: function () {
                this.timeBtnStatus = "hour"
                this.tableData.timemode = "Hour"
                this.$emit("UpperTable", this.tableData)
            },
            getWeekData: function () {
                this.timeBtnStatus = "weeks"
                this.tableData.timemode = "Week"
                this.$emit("UpperTable", this.tableData)
            },

            getWinRateData:function(){
                console.log("버튼 눌렸다!!!")
                this.graphBtnStatus = "winning-rate"
                this.tableData.gamemode = "Win"
                this.$emit("UpperTable", this.tableData)
            }
        },
    }
</script>