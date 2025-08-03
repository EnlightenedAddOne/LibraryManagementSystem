# 直接使用 nginx 提供静态文件服务
FROM nginx:alpine

# 复制已构建好的静态文件
COPY dist/ /usr/share/nginx/html/

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]