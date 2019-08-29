<template>
    <v-flex class="mt-5">
        <v-simple-table class="table-size" v-cloak>
            <thead bgcolor="#6799FF">
            <tr>
                <th style="color: white">순위</th>
                <th style="width:13%; color: white;">챔피언</th>
                <th colspan="1.5"></th>
                <th style="color: white">승률</th>
                <th style="color: white">플레이 수</th>
            </tr>
            </thead>
            <tbody v-show="lowerTable">
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
                    <div :style="{width: item.winRate +'px'}" class="graph"></div>
                    <span class="value-font value-color">{{item.winRate}}%</span>
                </td>
                <td align="left" class="value-font winRateValueColor">
                    {{item.playCount}} Play
                </td>
            </tr>
            </tbody>
        </v-simple-table>
        <v-layout class="LodingProgress" v-show="lowerTableLoading">
            <v-progress-circular color="primary" indeterminate size="170">데이터 받아오는 중...</v-progress-circular>
        </v-layout>
    </v-flex>
</template>

<script>
    export default {
        props: ['src', 'champions', 'lowerTableLoading', 'lowerTable']

    }
</script>

<style>
    .LodingProgress {
        display: block;
        margin-left: auto;
        margin-right: auto;
        width: 58%;
        height: 79%;
        border: 1px solid #d5d8d8;
        padding-left: 21.7%;
        padding-right: 15%;
        padding-top: 2.7%;
    }

    .winRateValueColor {
        margin-left: 0.2rem;
        color: #FF9900;
    }
</style>
