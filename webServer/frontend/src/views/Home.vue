<template>
    <v-container style="height: 100%">
        <Overlay :loading="loading"></Overlay>
        <v-layout column style="height: 100%" v-show="mainLoding">
            <MainTitle></MainTitle>
            <SearchBar></SearchBar>
            <UpperTable @UpperTable="getUpperTableData"></UpperTable>
            <LowerTable :champions="champions" :gamemodeText="gamemodeText"
                        :totalPickValue="totalPickValue"></LowerTable>
            <ErrorMessage :errorMessage="errorMessage"></ErrorMessage>
        </v-layout>
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
    import SockJS from 'sockjs-client'
    import Stomp from 'webstomp-client'

    export default {
        components: {
            HelloWorld, Main, UpperTable, ErrorMessage, LowerTable, MainTitle, SearchBar, Overlay
        },
        data() {
            return {
                loading: true,
                mainLoding: false,
                errorMessage: true,
                errorMessageMap: [],
                gamemodeText: "게임당 픽률",
                gamemode: "SoloRank",
                timemode: "Hour",
                totalPickValue: 0.0,
                totalPickValueMap: [],
                champions: [],
                map: [],
                championsMap: [],
                championsModeNames: ["SoloRank-Hour", "SoloRank-Day", "SoloRank-Week", "BAN-Hour", "BAN-Day", "BAN-Week"]
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

            console.log(this.map.findIndex(item => item.key === '1'))
            this.socket = new SockJS('http://localhost:8080/websocket-endpoint')
            this.stompClient = Stomp.over(this.socket)
            this.stompClient.connect({}, frame => {
                const baseURI = 'http://localhost:8080'
                this.$axios.get(`${baseURI}/api/getAllData`)
                    .then((result) => {
                        for (var i = 0; i < result.data.length; i++) {
                            var name = this.championsModeNames[i]
                            this.dataProcessMethod(name, result.data[i])
                        }
                        var curName = this.gamemode + "-" + this.timemode;
                        this.errorMessage = this.errorMessageMap[curName];
                        this.champions = this.championsMap[curName];
                        this.totalPickValue = this.totalPickValueMap[name];
                    })
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
                    this.dataProcessMethod(name, data.body)
                })
            },

            dataProcessMethod: function (name, data) {
                var parsed = JSON.parse(data.replace(/\\\"/ig, ""))
                this.loading = false;
                this.mainLoding = true;
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
                this.gamemode = table.gamemode;
                this.timemode = table.timemode;
                this.gamemodeText = table.gamemodeText;
                var name = this.gamemode + "-" + this.timemode;
                this.champions = this.championsMap[name];
                this.totalPickValue = this.totalPickValueMap[name];
                this.errorMessage = this.errorMessageMap[name];
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

