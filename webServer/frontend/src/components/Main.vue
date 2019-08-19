<template>
    <v-container style="height: 100%" >
        <v-layout column style="height: 100%">
            <v-flex xs2 >
                <v-card-title id="title-font" class="justify-center mt-10">
                    LOL DASHBOARD
                </v-card-title>
            </v-flex>
            <v-layout class="justify-center align-center mt-10">
                <v-flex xs10 color="white">
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
                                <v-radio label="승률" class="value-font"></v-radio>
                                <v-radio label="밴 픽률" class="value-font"></v-radio>
                                <v-radio label="게임당 픽률" class="value-font" ></v-radio>
                            </v-radio-group>
                        </td>
                        <td>
                            <v-radio-group class="ml-5">
                                <v-radio label="일주일 전" class="value-font"></v-radio>
                                <v-radio label="오늘" class="value-font"></v-radio>
                                <v-radio label="실시간" class="value-font"></v-radio>
                            </v-radio-group>
                        </td>

                        <td>
                            <v-radio-group class="ml-5">
                                <v-radio label="랭크게임" class="value-font"></v-radio>
                                <v-radio label="노말" class="value-font"></v-radio>
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
                        <th style="width:7%; color: white;">챔피언</th>
                        <th colspan="1.5"></th>
                        <th style="color: white">게임당 픽률</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="(item, index) in chamions" :key="item.name">
                        <td class="value-font ">{{index+1}}</td>
                        <td style="padding:0px;width:2rem;">
                            <img class="champion-cell ml-3"
                                 src="../assets/AnnieSquare.png"
                                 contain
                            ><img>
                        </td>
                        <td class="value-font">
                            {{ item.name }}
                        </td>
                        <td>
                            <div class="graph" :style="{width: item.pick+'px'}"></div>
                            <span class="value-font value-color">{{item.pick}}%</span>
                        </td>
                    </tr>
                    </tbody>
                </v-simple-table>
            </v-flex>

        </v-layout>
    </v-container>
</template>

<script>
    export default {
        data(){
            return {
                chamions:[
                    {
                        name: "애니",
                        pick: 78
                    },{
                        name: "이즈리얼",
                        pick: 15,
                    },{
                        name: "아리",
                        pick: 7
                    },{
                        name: "아트록스",
                        pick: 7
                    }
                    ,{
                        name: "유미",
                        pick: 7
                    }
                    ,{
                        name: "시비르",
                        pick: 7
                    }
                    ,{
                        name: "베인",
                        pick: 7
                    }
                ]
            }
        }

    }
</script>

<style>
    #title-font{
        font-size: 3.5rem;
        font-weight: bold;
    }

    .table-size{
        margin-left: auto;
        margin-right: auto;
        width: 83%;
        border: 1px solid #d5d8d8;
    }
    .champion-cell{
        margin-top: 1rem;
        display: block;
        width: 2rem;
        height: 2rem;
        border-radius: 50%;
        overflow: hidden;
    }
    .value-font{
        font-weight: bold;
        font-size: 0.75rem;
        font-family: Helvetica, "Malgun Gothic", "Apple SD Gothic Neo", AppleGothic, Dotum, Arial, Tahoma;
    }
    .value-color{
        margin-left: 0.2rem;
        color: #51758a;
    }
    .graph{
        display: inline-block;
        background-color: #1f8ecd;
        border: 1px solid;
        border-color: #1f8ecd;
        max-width: 60%;
        height: 0.5rem;
    }
</style>
