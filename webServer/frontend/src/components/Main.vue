<template>
    <v-container style="height: 100%">
        <v-overlay color="white" v-show="loading">
            <v-progress-circular
                    :size="150"
                    color="primary"
                    indeterminate
                    width="10"
            >데이터받는중...
            </v-progress-circular>
        </v-overlay>
        <v-layout column style="height: 100%" v-show="mainLoding">
            <v-flex xs2>
                <v-card-title class="justify-center mt-10" id="title-font">
                    LOL DASHBOARD
                </v-card-title>
            </v-flex>
            <v-layout class="justify-center mt-10">
                <v-flex color="white" xs7>
                    <v-text-field
                            label="소환사명을 입력해주세요."
                            outlined
                    ></v-text-field>
                </v-flex>
            </v-layout>
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
                                <v-radio class="value-font" label="승률" value="winning-rate"></v-radio>
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
                                <v-radio class="value-font" label="최근 1일" v-on:click="getDayData"
                                         value="days"></v-radio>
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
            <v-flex class="mt-5">
                <v-simple-table class="table-size" v-cloak>
                    <thead bgcolor="#6799FF">
                    <tr>
                        <th style="color: white">순위</th>
                        <th style="width:13%; color: white;">챔피언</th>
                        <th colspan="1.5"></th>
                        <th style="color: white">{{gamemodeText}}</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr :key="item.name" v-for="(item, index) in champions">
                        <td class="value-font" style="width: 7%">{{index+1}}</td>
                        <td style="padding:0px;width:2rem;">
                            <img class="champion-cell ml-3"
                                 contain
                                 v-bind:src="item.src"
                            >
                        </td>
                        <td class="value-font">
                            {{ item.championName }}
                        </td>
                        <td>
                            <div :style="{width: item.pick+'px'}" class="graph"></div>
                            <span class="value-font value-color">{{(item.pick / totalPickValue *100.0).toFixed(2)}}%</span>
                        </td>
                    </tr>
                    </tbody>
                </v-simple-table>
            </v-flex>
            <v-flex class="table-size" v-show="errorMessage" wrap>
                <div class="ErrorImage">
                    <img class="image" contain src="../assets/mark.png">
                </div>
                <div class="ErrorMessage">
                    데이터가 존재하지 않습니다. 다른 옵션을 선택해보세요.
                </div>
            </v-flex>

        </v-layout>
    </v-container>
</template>

<script>
    import SockJS from 'sockjs-client'
    import Stomp from 'webstomp-client'

    export default {
        data() {
            return {
                loading: true,
                mainLoding: false,
                errorMessage: false,
                errorMessageMap: [],
                gamemodeText: "게임당 픽률",
                gamemode: "SoloRank",
                timemode: "Hour",
                graphBtnStatus: "game-pick",
                timeBtnStatus: "hour",
                modeBtnStatus: "rank",
                totalPickValue: 0.0,
                champions: [],
                map: [],
                championsMap: []


            }
        },

        computed() {
        },

        mounted() {

            const config = require('../assets/champion.json')

            for (var key in config.data) {
                var member = new Object()
                member.name = config.data[key].name
                member.id = key
                member.key = config.data[key].key
                this.map.push(member)
                //console.log(config.data[key].key + "  " +config.data[key].name + " " + key)
            }

            console.log(this.map.findIndex(item => item.key === '1'))
            this.socket = new SockJS('http://localhost:8080/websocket-endpoint')
            this.stompClient = Stomp.over(this.socket)
            this.stompClient.connect({}, frame => {
                this.subscrbeFunction("/subscribe-server/ChampionData/SoloRank/Hour", "SoloRank-Hour")
                this.subscrbeFunction("/subscribe-server/ChampionData/SoloRank/Day", "SoloRank-Day")
                this.subscrbeFunction("/subscribe-server/ChampionData/SoloRank/Week", "SoloRank-Week")
                this.subscrbeFunction("/subscribe-server/ChampionData/BAN/Hour", "BAN-Hour")
                this.subscrbeFunction("/subscribe-server/ChampionData/BAN/Day", "BAN-Day")
                this.subscrbeFunction("/subscribe-server/ChampionData/BAN/Week", "BAN-Week")

            }, (error) => {
                console.log(error)

            })
        },

        methods: {
            send: function (curGameMode, curTimeMode) {
                this.loading = true;
                this.mainLoding = false;
                console.log('Send message:' + curGameMode + " " + curTimeMode)
                if (this.stompClient && this.stompClient.connected) {
                    var member = new Object()
                    member.gameMode = curGameMode
                    member.timeMode = curTimeMode
                    var message = JSON.stringify(member)
                    this.stompClient.send('/publish-server/to-client', message, {})
                }
            },

            subscrbeFunction: function (route, name) {
                this.stompClient.subscribe(route, (data) => {
                    //console.log(data)
                    var parsed = JSON.parse(data.body.replace(/\\\"/ig, ""))
                    this.loading = false;
                    this.mainLoding = true;
                    this.championsMap[name] = []
                    //this.champions = [];
                    this.totalPickValue = 0.0
                    if (parsed.length == 0) {
                        this.errorMessageMap[name] = true
                    } else {
                        this.errorMessageMap[name] = false
                    }

                    this.errorMessage = this.errorMessageMap[this.gamemode + "-" + this.timemode]
                    for (var i = 0; i < parsed.length; i++) {
                        var member = new Object()
                        var idx = this.map.findIndex(item => item.key === parsed[i].value)
                        if (idx == '-1') continue;
                        member.championName = this.map[idx].name
                        member.pick = parseFloat(parsed[i].score)
                        member.key = this.map[idx].id
                        member.src = require("../assets/championimg/" + this.map[idx].id + "_Square_0_1.jpg")
                        //this.champions.push(member)
                        this.championsMap[name].push(member)
                        this.totalPickValue += parseFloat(parsed[i].score)
                    }

                    console.log("total : " + this.totalPickValue)
                    this.championsMap[name].sort(function (itemA, itemB) {
                        return itemA.pick > itemB.pick ? -1 : itemA.pick < itemB.pick ? 1 : 0;
                    })
                })
            },

            getBannedData: function () {
                console.log("test")
                this.gamemode = "BAN"
                this.gamemodeText = "게임당 밴률"
                this.graphBtnStatus = "ban-pick"
                this.champions = this.championsMap[this.gamemode + "-" + this.timemode]
                //this.send(this.gamemode,this.timemode)
            },

            getChampionData: function () {
                console.log("test2")
                this.gamemode = "SoloRank"
                this.gamemodeText = "게임당 픽률"
                this.graphBtnStatus = "game-pick"
                this.champions = this.championsMap[this.gamemode + "-" + this.timemode]
                // this.send(this.gamemode, this.timemode)
            },

            getDayData: function () {
                this.timemode = "Day"
                this.timeBtnStatus = "days"
                this.champions = this.championsMap[this.gamemode + "-" + this.timemode]
                //this.send(this.gamemode, this.timemode)
            },

            getHourData: function () {
                this.timemode = "Hour"
                this.timeBtnStatus = "hour"
                this.champions = this.championsMap[this.gamemode + "-" + this.timemode]
                // this.send(this.gamemode, this.timemode)
            },

            getWeekData: function () {
                this.timemode = "Week"
                this.timeBtnStatus = "weeks"
                this.champions = this.championsMap[this.gamemode + "-" + this.timemode]
                // this.send(this.gamemode, this.timemode)
            }
        }

    }
</script>

<style>
    #title-font {
        font-size: 3.5rem;
        font-weight: bold;
    }

    .table-size {
        margin-left: auto;
        margin-right: auto;
        width: 58%;
        border: 1px solid #d5d8d8;
    }

    .champion-cell {
        margin-top: 0.2rem;
        display: block;
        width: 2rem;
        height: 2rem;
        border-radius: 50%;
        overflow: hidden;
    }

    .value-font {
        font-weight: bold;
        font-size: 0.75rem;
        font-family: Helvetica, "Malgun Gothic", "Apple SD Gothic Neo", AppleGothic, Dotum, Arial, Tahoma;
    }

    .value-color {
        margin-left: 0.2rem;
        color: #51758a;
    }

    .graph {
        display: inline-block;
        background-color: #1f8ecd;
        border: 1px solid;
        border-color: #1f8ecd;
        max-width: 60%;
        height: 0.5rem;
    }

    .ErrorMessage {
        display: block;
        padding: 10px 0 120px;
        text-align: center;
        font-size: 16px;
        font-weight: bold;
        color: #555e5e;
        align-content: center;
    }

    .ErrorImage {
        display: flex;
        padding-top: 60px;
        margin: auto;
    }

    .image {
        width: 100px;
        height: 100px;
        margin: auto; /* Magic! */
    }
</style>
