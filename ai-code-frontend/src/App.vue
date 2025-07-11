<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  height: 100%;
  width: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Inter', 'Helvetica Neue', sans-serif;
  background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 50%, #0d1117 100%);
  color: #f0f6fc;
  overflow: hidden;
}

body {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局发光效果 */
::selection {
  background: rgba(0, 212, 255, 0.3);
  color: #ffffff;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: rgba(13, 17, 23, 0.5);
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #7c3aed 0%, #00d4ff 100%);
  border-radius: 4px;
  box-shadow: 0 0 5px rgba(0, 212, 255, 0.3);
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #a855f7 0%, #00e5ff 100%);
}
</style>
