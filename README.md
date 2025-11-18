# Auth Demo – HTTPie Workspace

**auth_demo** là workspace dùng để thử nghiệm hệ thống Authentication & Authorization. Bao gồm các chức năng:

* Đăng ký
* Đăng nhập
* Login bằng Key
* Quên mật khẩu
* Reset mật khẩu
* Refresh token
* Logout
* Xem lịch sử đăng nhập
* Xem danh sách session đang hoạt động
* Thu hồi session
* Lấy thông tin user hiện tại
* Đổi mật khẩu
* Tạo key đăng nhập

Workspace được xuất từ **HTTPie Desktop**.

---

## 1. Collection: `auth`

Các endpoint chính:

| Endpoint                     | Method | Mô tả                             |
| ---------------------------- | ------ | --------------------------------- |
| `/auth/register`             | POST   | Đăng ký tài khoản                 |
| `/auth/login`                | POST   | Đăng nhập email/password          |
| `/auth/forget-password`      | POST   | Gửi yêu cầu lấy mã reset password |
| `/auth/reset-password?code=` | GET    | Xác minh reset token              |
| `/auth/reset-password`       | POST   | Đặt lại mật khẩu                  |
| `/auth/login-with-key`       | POST   | Đăng nhập bằng secret key         |
| `/auth/logout`               | POST   | Đăng xuất (dùng refresh token)    |
| `/auth/refresh-token`        | POST   | Lấy token mới                     |

---

## 2. Collection: `users`

Các endpoint phục vụ user:

| Endpoint                     | Method | Mô tả                           |
|------------------------------| ------ | ------------------------------- |
| `/users/me`                  | GET    | Lấy thông tin user hiện tại     |
| `/users/generate-key`        | GET    | Tạo key đăng nhập               |
| `/users/reset-password`      | POST   | Đổi mật khẩu khi đang đăng nhập |
| `/users/list-users`          | GET    | Admin xem danh sách user        |
| `/users/login-history`       | GET    | Xem log đăng nhập                 |
| `/users/sessions`            | GET    | Danh sách session                 |
| `/users/revoke-session/{id}` | POST   | Thu hồi session                   |

---

## 3. Environment

Workspace có biến môi trường:

```
BASE_URL = http://localhost:8080
```

---

## 4. Ghi chú

* Tất cả request yêu cầu xác thực dùng header `x-token: Bearer <TOKEN>`.
* Các request reset password dùng raw token hoặc token hash tùy API.
* Bạn có thể import file vào HTTPie Desktop để chạy thử toàn bộ flow.

---

## 5. HTTPie Workspace

Workspace này được export trực tiếp từ **HTTPie Desktop** và bao gồm đầy đủ request dùng để test toàn bộ flow của hệ thống.

### **Cách import vào HTTPie Desktop**

1. Mở HTTPie Desktop
2. Chọn **File → Import…**
3. Chọn file JSON workspace (auth_demo.json)
4. Workspace sẽ tự động tạo collections:

    * `auth`
    * `users`

### **Cấu trúc Workspace**

* **auth**: toàn bộ API liên quan đến xác thực
* **users**: API dành cho user đã đăng nhập
* Có sử dụng biến môi trường: `BASE_URL`
