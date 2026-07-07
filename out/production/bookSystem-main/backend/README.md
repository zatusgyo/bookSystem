# 后端开发

## 所用技术
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- Knife4j (Swagger)

## 项目结构
```
src/main/java/com/bookSystem/
├── BookSystemApplication.java    # 启动类
├── config/        # 配置类（CORS、MyBatis-Plus）
├── controller/    # 控制器（模板已创建）
├── service/       # 业务逻辑接口 + impl实现
├── mapper/        # 数据访问层
├── entity/        # 数据库实体
├── dto/           # 数据传输对象（请创建）
├── vo/            # 视图对象（请创建）
├── common/        # 通用类（Result、异常处理、分页）
└── util/          # 工具类
```

## 待完成任务
1. [ ] 补充 Service 完整业务逻辑（参考已有 UserServiceImpl/BorrowServiceImpl 模式）
2. [ ] 完善 Controller 的参数校验
3. [ ] 添加 JWT Token 认证
4. [ ] 接入支付宝/微信支付
5. [ ] 物流轨迹功能
6. [ ] 管理员后台接口
7. [ ] 编写单元测试

## 启动方式
```bash
cd backend
mvn spring-boot:run
```

## API 文档
启动后访问: http://localhost:8080/doc.html
