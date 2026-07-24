const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    client: {
      overlay: {
        runtimeErrors: (e) => {
          if (e.message.includes('ResizeObserver')) return false;
          return true;
        },
      },
    },
  },
})
