import { RoutesAlias } from '../routesAlias'
import { AppRouteRecord } from '@/types/router'
// import { WEB_LINKS } from '@/utils/constants'

/**
 * 菜单列表、异步路由
 *
 * 支持两种模式:
 * 1. 前端静态配置 - 直接使用本文件中定义的路由配置
 * 2. 后端动态配置 - 后端返回菜单数据，前端解析生成路由
 *
 * 菜单标题（title）:
 * 可以是 i18n 的 key，也可以是字符串，比如：'用户列表'
 */
export const asyncRoutes: AppRouteRecord[] = [
  {
    path: '/Home',
    name: 'HOME',
    component: RoutesAlias.HOME,
    meta: {
      title: '主页',
      icon: '&#xe731;'
    }
  },
  // {
  //   name: 'Dashboard',
  //   path: '/dashboard',
  //   component: RoutesAlias.Home,
  //   meta: {
  //     title: 'menus.dashboard.title',
  //     icon: '&#xe721;'
  //   },
  //   children: [
  //     {
  //       path: 'console',
  //       name: 'Console',
  //       component: RoutesAlias.Dashboard,
  //       meta: {
  //         title: 'menus.dashboard.console',
  //         keepAlive: false,
  //         fixedTab: true
  //       }
  //     },
  //     {
  //       path: 'analysis',
  //       name: 'Analysis',
  //       component: RoutesAlias.Analysis,
  //       meta: {
  //         title: 'menus.dashboard.analysis',
  //         keepAlive: false
  //       }
  //     }
  //   ]
  // },
  // 系统管理
  {
    path: '/system',
    name: 'System',
    component: RoutesAlias.Home,
    meta: {
      title: 'menus.system.title',
      icon: '&#xe724',
      roles: ['R_SUPER', 'R_ADMIN']
    },
    children: [
      {
        path: 'user',
        name: 'User',
        component: RoutesAlias.User,
        meta: {
          title: 'menus.system.user',
          keepAlive: true,
          roles: ['R_SUPER', 'R_ADMIN']
        }
      },
      {
        path: 'role',
        name: 'Role',
        component: RoutesAlias.Role,
        meta: {
          title: 'menus.system.role',
          keepAlive: true,
          roles: ['R_SUPER']
        }
      },
      {
        path: 'user-center',
        name: 'UserCenter',
        component: RoutesAlias.UserCenter,
        meta: {
          title: 'menus.system.userCenter',
          isHide: true,
          keepAlive: true,
          isHideTab: true
        }
      }
    ]
  },
  // {
  //   path: '/article',
  //   name: 'Article',
  //   component: RoutesAlias.Home,
  //   meta: {
  //     title: 'menus.article.title',
  //     icon: '&#xe7ae;',
  //     roles: ['R_SUPER', 'R_ADMIN']
  //   },
  //   children: [
  //     {
  //       path: 'article-list',
  //       name: 'ArticleList',
  //       component: RoutesAlias.ArticleList,
  //       meta: {
  //         title: 'menus.article.articleList',
  //         keepAlive: true,
  //         authList: [
  //           {
  //             title: '新增',
  //             auth_mark: 'add'
  //           },
  //           {
  //             title: '编辑',
  //             auth_mark: 'edit'
  //           }
  //         ]
  //       }
  //     },

  //     {
  //       path: 'detail',
  //       name: 'ArticleDetail',
  //       component: RoutesAlias.ArticleDetail,
  //       meta: {
  //         title: 'menus.article.articleDetail',
  //         isHide: true,
  //         keepAlive: true
  //       }
  //     }
  //   ]
  // },

  {
    path: '/books',
    name: 'Books',
    component: RoutesAlias.Home,
    meta: {
      title: '图书管理',
      icon: '&#xe7bc;',
      roles: ['R_SUPER', 'R_ADMIN']
    },
    children: [
      {
        path: 'booklist',
        name: 'BookList',
        component: RoutesAlias.BookList, // 图书列表组件
        meta: {
          title: '图书列表'
        }
      },
      {
        path: 'addbook',
        name: 'AddBook',
        component: RoutesAlias.AddBook, // 图书列表组件
        meta: {
          title: '添加图书',
          roles: ['R_SUPER', 'R_ADMIN']
        }
      }
    ]
  },
  {
    path: '/borrow',
    name: 'Borrow',
    component: RoutesAlias.Home,
    meta: {
      title: '借阅管理',
      icon: '&#xe651;'
    },
    children: [
      {
        path: 'borrow_records',
        name: 'BorrowRecords',
        component: RoutesAlias.BorrowRecords, // 图书借阅列表
        meta: {
          title: '借阅记录',
          roles: ['R_SUPER', 'R_ADMIN']
        }
      },
      {
        path: 'book_borrow',
        name: 'BookBorrow',
        component: RoutesAlias.BookBorrow, // 图书借阅
        meta: {
          title: '图书借阅'
        }
      },
      {
        path: 'personal_records',
        name: 'PersonalRecords',
        component: RoutesAlias.PersonalRecords, // 个人借阅记录
        meta: {
          title: '个人借阅记录'
          // keepAlive: false,
          // icon: '&#xe828;'
        }
      }
    ]
  }
  // {
  //   name: 'Help',
  //   path: '/help',
  //   component: RoutesAlias.Home,
  //   meta: {
  //     title: 'menus.help.title',
  //     icon: '&#xe719;',
  //     keepAlive: false,
  //     roles: ['R_SUPER', 'R_ADMIN']
  //   },
  //   children: [
  //     {
  //       path: '',
  //       name: 'Document',
  //       meta: {
  //         title: 'menus.help.document',
  //         link: WEB_LINKS.DOCS,
  //         isIframe: false,
  //         keepAlive: false
  //       }
  //     }
  //   ]
  // }
]
