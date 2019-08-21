<template>
    <div>
        <button @click="connect"> 접속하기</button>
        <div>
            <input placeholder="Send Message" v-model="send_message">
            <button @click="send">Send</button>
        </div>
        <h1> 보낸 메세지 </h1>
        {{sendmessage}}

        <h1> 받은 메세지 </h1>
        {{receivedmessage}}

    </div>
</template>

<script>
    import SockJS from 'sockjs-client'
    import Stomp from 'webstomp-client'

    export default {
        name: 'test2',

        data() {
            return {
                sendmessage: null,
                receivedmessage: [],
                send_message: null,
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
                console.log(config.data[key].key + "  " + config.data[key].name + " " + key)
            }

            console.log(this.map.findIndex(item => item.key === '1'))
            this.socket = new SockJS('http://localhost:8080/websocket-endpoint')
            this.stompClient = Stomp.over(this.socket)
            this.stompClient.connect({}, frame => {
                this.stompClient.subscribe('/subscribe-server/ChampionData', (data) => {
                    console.log("data : " + data.body)
                    var parsed = JSON.parse(data.body.replace(/\\\"/ig, ""))
                    this.receivedmessage = [];
                    for (var i = 0; i < parsed.length; i++) {
                        var member = new Object()
                        var idx = this.map.findIndex(item => item.key === parsed[i].value)
                        member.championName = this.map[idx].name;
                        member.pick = parsed[i].score;
                        this.receivedmessage.push(member)
                    }
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
