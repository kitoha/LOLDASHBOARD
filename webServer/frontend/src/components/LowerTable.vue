<template>
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
                    <div :style="{width: (item.pick / totalPickValue *100.0).toFixed(2)+'px'}" class="graph"></div>
                    <span class="value-font value-color">{{(item.pick / totalPickValue *100.0).toFixed(2)}}%</span>
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
        props: ['gamemodeText', 'champions', 'totalPickValue', 'lowerTableLoading', 'lowerTable']
    }
</script>
