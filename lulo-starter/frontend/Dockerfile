# Front-end Dockerfile
# En situaciones normales, usaríamos un build de Node, pero como no hay package.json,
# asumimos que los archivos estáticos se encuentran en src para propósitos de demostración.
FROM nginx:alpine
COPY ./src /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
