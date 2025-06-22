<!-- BookStatistics.vue -->
<template>
  <el-row :gutter="20" :style="{ marginTop: showWorkTab ? '0' : '10px' }" class="card-list">
    <el-col v-for="(item, index) in dataList" :key="index" :sm="12" :md="6" :lg="6">
      <div class="card art-custom-card statistics-card">
        <div class="card-content">
          <div class="text-content">
            <span class="des subtitle">{{ item.des }}</span>
            <CountTo
              class="number box-title"
              :endVal="item.num"
              :duration="1000"
              separator=","
            ></CountTo>
            <div class="change-box">
              <span class="change-text">较上月</span>
              <span
                class="change"
                :class="[item.change.indexOf('+') === -1 ? 'text-danger' : 'text-success']"
              >
                {{ item.change }}
              </span>
            </div>
          </div>
          <div class="icon-container">
            <i class="iconfont-sys" v-html="item.icon"></i>
          </div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
  import { reactive } from 'vue'
  import { storeToRefs } from 'pinia'
  import { useSettingStore } from '@/store/modules/setting'
  import { CountTo } from 'vue3-count-to'

  const { showWorkTab } = storeToRefs(useSettingStore())

  const dataList = reactive([
    {
      des: '馆藏图书总数',
      icon: '&#xe721;',
      startVal: 0,
      duration: 1000,
      num: 15680,
      change: '+8%'
    },
    {
      des: '在馆读者数',
      icon: '&#xe724;',
      startVal: 0,
      duration: 1000,
      num: 152,
      change: '+15%'
    },
    {
      des: '本月借阅量',
      icon: '&#xe7aa;',
      startVal: 0,
      duration: 1000,
      num: 3240,
      change: '+12%'
    },
    {
      des: '新增读者',
      icon: '&#xe82a;',
      startVal: 0,
      duration: 1000,
      num: 68,
      change: '+25%'
    }
  ])
</script>

<style lang="scss" scoped>
  .card-list {
    margin-bottom: 20px;

    .statistics-card {
      height: 10px; // 从140px减少到110px
      padding: 0;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 1px solid var(--el-border-color-lighter);

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        border-color: var(--el-color-primary-light-7);
      }

      .card-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 100%;
        padding: 15px 16px; // 从20px 18px减少到15px 16px
        box-sizing: border-box;

        .text-content {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: center;
          height: 100%;

          .des {
            font-size: 13px; // 从14px减少到13px
            color: var(--art-gray-600);
            margin-bottom: 6px; // 从8px减少到6px
            font-weight: 500;
            line-height: 1.2;
          }

          .number {
            font-size: 26px; // 从32px减少到26px
            font-weight: 700;
            color: var(--art-gray-900);
            margin: 4px 0; // 从6px减少到4px
            line-height: 1.1;
          }

          .change-box {
            display: flex;
            align-items: center;
            margin-top: 2px; // 从4px减少到2px

            .change-text {
              font-size: 11px; // 从12px减少到11px
              color: var(--art-gray-500);
              margin-right: 6px;
            }

            .change {
              font-size: 12px; // 从13px减少到12px
              font-weight: 600;

              &.text-success {
                color: var(--el-color-success);
              }

              &.text-danger {
                color: var(--el-color-danger);
              }
            }
          }
        }

        .icon-container {
          flex-shrink: 0;
          width: 50px; // 从60px减少到50px
          height: 50px; // 从60px减少到50px
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 10px; // 从12px减少到10px
          background: var(--el-color-primary-light-9);
          transition: all 0.3s ease;

          .iconfont-sys {
            font-size: 24px; // 从28px减少到24px
            color: var(--el-color-primary);
            transition: all 0.3s ease;
          }
        }
      }

      &:hover {
        .icon-container {
          background: var(--el-color-primary-light-8);
          transform: scale(1.05);

          .iconfont-sys {
            color: var(--el-color-primary-dark-2);
          }
        }
      }
    }
  }

  // 响应式设计
  @media screen and (max-width: 992px) {
    .card-list {
      .statistics-card {
        height: 100px; // 从130px减少到100px

        .card-content {
          padding: 12px 14px; // 从16px减少

          .text-content {
            .number {
              font-size: 22px; // 从28px减少到22px
            }
          }

          .icon-container {
            width: 42px; // 从50px减少到42px
            height: 42px;

            .iconfont-sys {
              font-size: 20px; // 从24px减少到20px
            }
          }
        }
      }
    }
  }

  @media screen and (max-width: 768px) {
    .card-list {
      .statistics-card {
        height: 90px; // 从120px减少到90px

        .card-content {
          padding: 10px 12px; // 从15px减少

          .text-content {
            .des {
              font-size: 12px; // 从13px减少到12px
              margin-bottom: 4px;
            }

            .number {
              font-size: 20px; // 从24px减少到20px
              margin: 2px 0;
            }

            .change-box {
              margin-top: 1px;

              .change-text {
                font-size: 10px; // 从11px减少到10px
              }

              .change {
                font-size: 11px; // 从12px减少到11px
              }
            }
          }

          .icon-container {
            width: 38px; // 从45px减少到38px
            height: 38px;
            border-radius: 8px;

            .iconfont-sys {
              font-size: 18px; // 从20px减少到18px
            }
          }
        }
      }
    }
  }

  @media screen and (max-width: 576px) {
    .card-list {
      .statistics-card {
        height: 85px; // 从110px减少到85px

        .card-content {
          padding: 8px 10px; // 从12px减少

          .text-content {
            .des {
              font-size: 11px;
              margin-bottom: 3px;
            }

            .number {
              font-size: 18px; // 从22px减少到18px
              margin: 1px 0;
            }

            .change-box {
              .change-text {
                font-size: 9px;
              }

              .change {
                font-size: 10px;
              }
            }
          }

          .icon-container {
            width: 34px; // 从40px减少到34px
            height: 34px;

            .iconfont-sys {
              font-size: 16px; // 从18px减少到16px
            }
          }
        }
      }
    }
  }

  // 深色模式适配
  .dark {
    .statistics-card {
      border-color: #374151;

      &:hover {
        border-color: var(--el-color-primary-light-5);
      }

      .card-content {
        .text-content {
          .des {
            color: #9ca3af;
          }

          .number {
            color: #f3f4f6;
          }

          .change-box {
            .change-text {
              color: #6b7280;
            }
          }
        }

        .icon-container {
          background: #1f2937;

          .iconfont-sys {
            color: var(--el-color-primary-light-3);
          }
        }
      }

      &:hover {
        .icon-container {
          background: #374151;
        }
      }
    }
  }
</style>
