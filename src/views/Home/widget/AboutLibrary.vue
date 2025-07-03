<template>
  <div class="card about-project art-custom-card">
    <div class="content">
      <h3 class="box-title">关于图书馆</h3>
      <p class="description">{{ libraryName }} 是一座现代化的智慧图书馆</p>
      <p class="description">采用了最新的图书管理系统，提供便捷高效的借阅服务</p>

      <div class="button-wrap">
        <div class="btn art-custom-button" @click="goPage('/catalog')">
          <span>图书检索</span>
          <i class="iconfont-sys">&#xe703;</i>
        </div>
        <div class="btn art-custom-button" @click="goPage('/services')">
          <span>服务指南</span>
          <i class="iconfont-sys">&#xe703;</i>
        </div>
        <div class="btn art-custom-button" @click="goPage('/reader')">
          <span>读者服务</span>
          <i class="iconfont-sys">&#xe703;</i>
        </div>
        <div class="btn art-custom-button" @click="goPage('/digital')">
          <span>数字资源</span>
          <i class="iconfont-sys">&#xe703;</i>
        </div>
      </div>
    </div>

    <!-- 使用占位图片或默认图片 src/assets/img/home_card_bg\卡提西亚.png-->
    <div class="image-container">
      <img
        src="@/assets/img/home_card_bg/role.png"
        alt="智慧图书馆"
        class="library-image"
        loading="lazy"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router'

  const router = useRouter()
  const libraryName = '智慧图书馆管理系统'

  const goPage = (path: string) => {
    router.push(path)
  }
</script>

<style lang="scss" scoped>
  .about-project {
    display: flex;
    justify-content: space-between;
    align-items: flex-end; /* 改为底部对齐 */
    min-height: 300px;
    padding: 25px;
    box-sizing: border-box;
    position: relative; /* 添加相对定位 */
    overflow: hidden;
  }

  .content {
    position: relative;
    z-index: 2;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    min-height: 250px;

    h2 {
      margin: 0 0 15px 0;
      font-size: 24px;
      font-weight: 600;
      color: var(--art-gray-900);
      line-height: 1.3;
    }

    .description {
      margin: 0 0 10px 0;
      font-size: 15px;
      color: var(--art-gray-600);
      line-height: 1.6;

      &:last-of-type {
        margin-bottom: 25px;
      }
    }
  }

  .image-container {
    position: absolute;
    right: 0;
    bottom: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
    pointer-events: none;
    margin: 0;
    padding: 0;
    border-radius: 0;
    opacity: 0.6;
    display: flex;
    justify-content: flex-end;
    align-items: flex-end;
  }

  .library-image {
    max-width: 50%; // 或你想要的比例
    max-height: 100%;
    object-fit: contain;
    display: block;
  }

  .button-wrap {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    margin-top: auto;
    width: 100%;
    max-width: 500px;
  }

  .btn {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 52px;
    padding: 0 18px;
    font-size: 14px;
    font-weight: 500;
    color: var(--art-gray-800);
    cursor: pointer;
    background: var(--art-bg-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: calc(var(--custom-radius));
    transition: all 0.3s ease;
    box-sizing: border-box;

    &:hover {
      background: var(--el-color-primary-light-9);
      border-color: var(--el-color-primary-light-6);
      color: var(--el-color-primary);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    span {
      flex: 1;
      text-align: left;
    }

    .iconfont-sys {
      font-size: 16px;
      color: var(--el-color-primary);
      transition: transform 0.3s ease;
    }

    &:hover .iconfont-sys {
      transform: translateX(2px);
    }
  }

  // 响应式设计
  @media screen and (max-width: 1200px) {
    .about-project {
      display: flex;
      align-items: center;
      .image-container {
        width: 240px;
        height: 180px;
        margin-left: 20px;
      }
    }
  }

  @media screen and (max-width: 992px) {
    .about-project {
      flex-direction: column;
      min-height: auto;

      .content {
        min-height: auto;
        margin-bottom: 20px;

        h2 {
          font-size: 22px;
        }
      }

      .image-container {
        width: 100%;
        height: 160px;
        margin: 0;
      }
    }

    .button-wrap {
      max-width: none;
    }
  }

  @media screen and (max-width: 768px) {
    .about-project {
      padding: 20px;

      .content {
        h2 {
          font-size: 20px;
        }

        .description {
          font-size: 14px;
        }
      }

      .image-container {
        height: 140px;

        .placeholder-img {
          .library-icon {
            font-size: 36px;
          }

          p {
            font-size: 14px;
          }
        }
      }
    }
  }

  @media screen and (max-width: 576px) {
    .button-wrap {
      grid-template-columns: 1fr;
    }

    .about-project {
      padding: 15px;

      .image-container {
        height: 120px;
      }
    }
  }

  // 深色模式适配
  .dark {
    .about-project {
      .image-container {
        .placeholder-img {
          background: linear-gradient(135deg, #1f2937 0%, #111827 100%);
          border-color: #374151;
          color: var(--el-color-primary-light-3);
        }
      }
    }

    .btn {
      background: #1f2937;
      border-color: #374151;
      color: #d1d5db;

      &:hover {
        background: #374151;
        border-color: var(--el-color-primary);
        color: var(--el-color-primary-light-3);
      }
    }
  }
</style>
