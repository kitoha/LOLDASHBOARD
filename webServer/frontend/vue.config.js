'use strict'

module.exports = {
    devServer: {
        proxy: { // proxyTable 설정
            '/api': {
                target: 'http://localhost:8080'
            },
            '/websocket-endpoint': {
                target: 'http://localhost:8080'
            }
        }
    }
}