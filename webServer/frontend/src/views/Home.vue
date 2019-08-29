<template>
    <v-container style="height: 100%">
        <Overlay :loading="loading"></Overlay>
        <v-layout column style="height: 100%" v-show="mainLoding">
            <MainTitle></MainTitle>
            <SearchBar></SearchBar>
            <UpperTable @UpperTable="getUpperTableData"></UpperTable>
            <LowerTable :champions="champions" :gamemodeText="gamemodeText"
                        :lowerTable="lowerTable"
                        :lowerTableLoading="lowerTableLoading"
                        :totalPickValue="totalPickValue"
                        v-show="LowerTableStatus"></LowerTable>
            <WinRateLowerTable :champions="this.champions" :lowerTable="lowerTable"
                               :lowerTableLoading="lowerTableLoading"
                               :src="this.winRateSrc"
                               v-show="winRateLoding"></WinRateLowerTable>
            <ErrorMessage :errorMessage="errorMessage"></ErrorMessage>
        </v-layout>
        <MainErrorMessage :errorMessage="mainErrorMessage" v-show="mainErrorChecker"></MainErrorMessage>
    </v-container>
</template>

<script>
    import HelloWorld from '../components/HelloWorld';
    import Main from '../components/Main';
    import UpperTable from '../components/UpperTable'
    import ErrorMessage from '../components/ErrorMessage'
    import LowerTable from '../components/LowerTable'
    import SearchBar from '../components/SearchBar'
    import MainTitle from '../components/MainTitle'
    import Overlay from '../components/Overlay'
    import WinRateLowerTable from '../components/WinRateLowerTable'
    import MainErrorMessage from '../components/MainErrorMessage'
    import SockJS from 'sockjs-client'
    import Stomp from 'webstomp-client'
    import {setTimeout} from 'timers';

    export default {
        components: {
            HelloWorld,
            Main,
            UpperTable,
            ErrorMessage,
            LowerTable,
            MainTitle,
            SearchBar,
            Overlay,
            WinRateLowerTable,
            MainErrorMessage
        },
        data() {
            return {
                loading: true,
                mainLoding: false,
                mainErrorChecker: false,
                mainErrorMessage: "error",

                /*로딩 변수*/
                lowerTableLoading: false,
                lowerTable: true,

                /*초기 세팅변수 */
                map: [],

                /*에러 메세지 변수*/
                errorMessage: true,
                errorMessageMap: [],
                /*게임 상태 변수*/
                gamemodeText: "게임당 픽률",
                gamemode: "SoloRank",
                timemode: "Hour",
                champions: [],

                /*게임픽률 변수*/
                totalPickValue: 0.0,
                totalPickValueMap: [],
                championsMap: [],
                LowerTableStatus: true,

                /*subscribe 변수*/
                championsModeNames: ["SoloRank-Hour", "SoloRank-Day", "SoloRank-Week", "BAN-Hour", "BAN-Day", "BAN-Week"],

                /*승률 테이블 변수*/
                winRateLoding: false,
                winRateSrc: []
            }
        },

        mounted() {
            const config = require('../assets/champion.json')
            for (var key in config.data) {
                var member = new Object()
                member.name = config.data[key].name
                member.id = key
                member.key = config.data[key].key
                this.map.push(member)
            }
            this.socket = new SockJS('/websocket-endpoint')
            this.stompClient = Stomp.over(this.socket)
            this.stompClient.debug = () => {
            };
            this.stompClient.connect({}, frame => {
                this.$axios.get('/api/getAllData')
                    .then((result) => {
                        for (var i = 0; i < result.data.length; i++) {
                            var name = this.championsModeNames[i]
                            this.dataProcessMethod(name, result.data[i])
                        }
                        var curName = this.gamemode + "-" + this.timemode;
                        this.errorMessage = this.errorMessageMap[curName];
                        this.champions = this.championsMap[curName];
                        this.totalPickValue = this.totalPickValueMap[curName];
                        this.loading = false;
                        this.mainLoding = true;
                    }).catch((error) => {
                    this.mainErrorMessage = "404 not Found Api Call Failed";
                    this.loading = false;
                    this.mainErrorChecker = true;
                })
                this.subscrbeFunction("/subscribe-server/ChampionData/SoloRank/Hour", "SoloRank-Hour")
                this.subscrbeFunction("/subscribe-server/ChampionData/SoloRank/Day", "SoloRank-Day")
                this.subscrbeFunction("/subscribe-server/ChampionData/SoloRank/Week", "SoloRank-Week")
                this.subscrbeFunction("/subscribe-server/ChampionData/BAN/Hour", "BAN-Hour")
                this.subscrbeFunction("/subscribe-server/ChampionData/BAN/Day", "BAN-Day")
                this.subscrbeFunction("/subscribe-server/ChampionData/BAN/Week", "BAN-Week")
            }, (error) => {
                console.log(error)
                this.mainErrorMessage = "404 not Found WebSocket Connected Failed";
                this.loading = false;
                this.mainErrorChecker = true;

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
                    this.dataProcessMethod(name, data.body)
                })
            },
            getWinRateChampionsData: function () {
                this.$axios.get(`/api/winrate/${this.timemode}`)
                    .then((result) => {
                        var data = result.data;
                        this.champions = [];

                        for (var i = 0; i < data.length; i++) {
                            var member = new Object();
                            member.winRate = data[i].winRate;
                            var championId = data[i].id.replace(/\"/gi, "");
                            var idx = this.map.findIndex(item => item.key === championId)
                            member.src = require("../assets/championimg/" + this.map[idx].id + "_Square_0_1.jpg");
                            member.championName = this.map[idx].name;
                            member.playCount = data[i].playCount;
                            this.champions.push(member);
                        }

                        this.lowerTableLoading = false;
                        this.lowerTable = true;

                        this.champions.sort(function (itemA, itemB) {
                            return itemA.winRate > itemB.winRate ? -1 : itemA.winRate < itemB.winRate ? 1 : 0;
                        })
                    }).catch((error) => {
                    this.mainErrorMessage = "404 not Found Api Call Failed";
                    this.mainLoding = false;
                    this.mainErrorChecker = true;
                })
            },

            dataProcessMethod: function (name, data) {
                var parsed = JSON.parse(data.replace(/\\\"/ig, ""))
                this.championsMap[name] = []
                this.totalPickValueMap[name] = 0.0
                this.errorMessageMap[name] = true
                for (var i = 0; i < parsed.length; i++) {
                    var member = new Object()
                    var idx = this.map.findIndex(item => item.key === parsed[i].value)
                    if (idx == '-1') continue;
                    member.championName = this.map[idx].name
                    member.pick = parseFloat(parsed[i].score)
                    member.key = this.map[idx].id
                    member.src = require("../assets/championimg/" + this.map[idx].id + "_Square_0_1.jpg")
                    this.championsMap[name].push(member)
                    this.totalPickValueMap[name] += parseFloat(parsed[i].score)
                }
                if (this.isEmpty(this.championsMap[name])) {
                    this.errorMessageMap[name] = true;
                } else {
                    this.errorMessageMap[name] = false;
                }
                this.championsMap[name].sort(function (itemA, itemB) {
                    return itemA.pick > itemB.pick ? -1 : itemA.pick < itemB.pick ? 1 : 0;
                })
            },
            isEmpty: function (obj) {
                for (var key in obj) {
                    if (obj.hasOwnProperty(key)) {
                        return false;
                    }
                }
                return true;
            },
            getUpperTableData: function (table) {
                this.lowerTableLoading = true;
                this.lowerTable = false;
                this.gamemode = table.gamemode;
                this.timemode = table.timemode;
                var name = this.gamemode + "-" + this.timemode;
                this.gamemodeText = table.gamemodeText;
                this.errorMessage = false;

                if (this.gamemode == "WinRate" && this.timemode != "Hour") {
                    this.getWinRateChampionsData();
                    this.winRateLoding = true;
                    this.LowerTableStatus = false;
                } else {
                    this.champions = this.championsMap[name];
                    this.totalPickValue = this.totalPickValueMap[name];
                    this.LowerTableStatus = true;
                    this.winRateLoding = false;
                    if (this.isEmpty(this.champions)) {
                        this.errorMessageMap[name] = true;
                    } else {
                        this.errorMessageMap[name] = false;
                    }
                    this.errorMessage = this.errorMessageMap[name];

                    setTimeout(() => {
                        this.lowerTableLoading = false;
                        this.lowerTable = true;
                    }, 1000)

                }
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
