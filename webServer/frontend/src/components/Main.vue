<template>
    <v-container style="height: 100%">
        <v-layout column style="height: 100%">
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
                            <v-radio-group class="ml-5">
                                <v-radio class="value-font" label="승률"></v-radio>
                                <v-radio class="value-font" label="밴 픽률"></v-radio>
                                <v-radio class="value-font" label="게임당 픽률"></v-radio>
                            </v-radio-group>
                        </td>
                        <td>
                            <v-radio-group class="ml-5">
                                <v-radio class="value-font" label="일주일 전"></v-radio>
                                <v-radio class="value-font" label="오늘"></v-radio>
                                <v-radio class="value-font" label="실시간"></v-radio>
                            </v-radio-group>
                        </td>

                        <td>
                            <v-radio-group class="ml-5">
                                <v-radio class="value-font" label="랭크게임"></v-radio>
                                <v-radio class="value-font" label="노말"></v-radio>
                            </v-radio-group>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </v-flex>
            <v-flex class="mt-5">
                <v-simple-table class="table-size">
                    <thead bgcolor="#6799FF">
                    <tr>
                        <th style="color: white">순위</th>
                        <th style="width:13%; color: white;">챔피언</th>
                        <th colspan="1.5"></th>
                        <th style="color: white">게임당 픽률</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr :key="item.name" v-for="(item, index) in champions">
                        <td class="value-font" style="width: 7%">{{index+1}}</td>
                        <td style="padding:0px;width:2rem;">
                            <img class="champion-cell ml-3"
                                 contain
                                 v-bind:src="item.src"
                            ><img>
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

        </v-layout>
    </v-container>
</template>

<script>
    import SockJS from 'sockjs-client'
    import Stomp from 'webstomp-client'

    export default {
        data() {
            return {
                totalPickValue: 0.0,
                champions: [],
                map: []
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
                //console.log(config.data[key].key + "  " +config.data[key].name + " " + key)
            }

            console.log(this.map.findIndex(item => item.key === '1'))
            this.socket = new SockJS('http://localhost:8080/websocket-endpoint')
            this.stompClient = Stomp.over(this.socket)
            this.stompClient.connect({}, frame => {
                this.stompClient.subscribe('/subscribe-server/ChampionData', (data) => {
                    var parsed = JSON.parse(data.body.replace(/\\\"/ig, ""))
                    console.log(parsed)
                    this.champions = [];
                    this.totalPickValue = 0.0
                    for (var i = 0; i < parsed.length; i++) {
                        var member = new Object()
                        var idx = this.map.findIndex(item => item.key === parsed[i].value)
                        member.championName = this.map[idx].name
                        member.pick = parseFloat(parsed[i].score)
                        member.key = this.map[idx].id
                        member.src = require("../assets/championimg/" + this.map[idx].id + "_Square_0_1.jpg")
                        this.champions.push(member)
                        this.totalPickValue += parseFloat(parsed[i].score)
                    }
                    console.log("total : " + this.totalPickValue)

                    this.champions.sort(function (itemA, itemB) {
                        return itemA.pick > itemB.pick ? -1 : itemA.pick < itemB.pick ? 1 : 0;
                    })
                })

            }, (error) => {
                console.log(error)

            })


        },

        methods: {
            send() {
                console.log('Send message:' + this.send_message)
                if (this.stompClient && this.stompClient.connected) {
                    this.stompClient.send('/publish-server/to-client', this.send_message, {})
                }
            },

            connect() {

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
        margin-top: 1rem;
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
</style>
