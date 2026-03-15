<template>
  <div class="hello">
    <h1>{{ message }}</h1>
    <button @click="increment">点击次数: {{ count }}</button>
    <p v-if="showMessage">这是一条条件显示的消息</p>
    <input v-model="inputText" placeholder="输入一些文字">
    <p>你输入的是: {{ inputText }}</p>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  data() {
    return {
      message: '欢迎使用 Vue!',
      count: 0,
      showMessage: true,
      inputText: ''
    }
  },
  methods: {
    increment() {
      this.count++
      this.showMessage = !this.showMessage
    }
  }
}
</script>

<style scoped>
.hello {
  text-align: center;
  padding: 20px;
}
button {
  margin: 10px;
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
input {
  padding: 8px;
  margin: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>
