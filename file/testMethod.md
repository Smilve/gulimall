# 授权码模式 
# 先请求授权码，访问服务器请求接口，把授权码给服务器，然后服务器请求token，再获取用户信息或访问资源
请求授权码：localhost:8000/oauth/authorize?client_id=client1&response_type=code&scope=scope1
&redirect_uri=http://www.baidu.com

请求token：localhost:8000/oauth/token?client_id=client1&client_secret=123123&grant_type=authorization_code&code=lDU3p7&redirect_uri=http://www.baidu.com

# 简化模式  一般的请求都是post请求
# 直接请求token，一般用于没有服务器的第三方单页面应用，因为没有服务器就不能接受授权码
请求token：localhost:8000/oauth/authorize?client_id=client1&response_type=token&scope=scope1
&redirect_uri=http://www.baidu.com

# 密码模式
# 会把用户敏感信息泄漏给client，一般用于自己开发，第一方原生app或第一方单页面应用
请求token：localhost:8000/oauth/token?client_id=client1&client_secret=123123&grant_type=password&username=test&password=123

# 客户端模式
# 最方便但最不安全的，要求对客户端必须完全信任，一般用来提供给完全信任的服务端服务，如合作方系统对接，拉取一组用户信息
请求token：localhost:8000/oauth/token?client_id=client1&client_secret=123123&grant_type=client_credentials

当访问需要权限的资源时：请求头需要带上：Authorization:Bearer token