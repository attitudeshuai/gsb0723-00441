<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <p class="error-text">页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use "@/assets/styles/variables.scss" as *;

.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $bg-base;
}

.error-content {
  text-align: center;

  .error-code {
    font-size: 120px;
    font-weight: 700;
    color: $primary-accent;
    margin: 0;
    line-height: 1;
    text-shadow: 4px 4px 0 rgba($primary-accent, 0.1);
  }

  .error-text {
    font-size: 24px;
    color: $text-regular;
    margin: 20px 0 40px;
    font-weight: 500;
  }
}
</style>
