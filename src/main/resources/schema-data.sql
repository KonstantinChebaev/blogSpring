INSERT INTO `blog`.`users` (`id`, `email`, `is_moderator`, `name`, `password`, `reg_time`) 
VALUES ('1', 'user@user.com', '0', 'user', '{bcrypt}$2a$10$Wx6AX80Al7Khp4h/x4838uVvGaynqczP6BrwWG/2zVXHacBAn.36u', '2020-07-03 18:38:21'),
('2', 'admin@admin.com', '1', 'admin', '$2a$10$TCyBMXYUnwjr/lzKRw1doOMS4rNFLfeJSIJUf7AtHpbtNBcX9Sdbe', '2020-07-29 18:38:21'),
('3', 'moder@moder.com', '1', 'moder', 'moder@moder.com', '2020-08-09 18:38:21');

INSERT INTO `blog`.`posts` (`id`, `is_active`, `moderation_status`, `moderator_id`, `text`, `time`, `title`, `view_count`, `user_id`) 
VALUES ('1', '1', 'ACCEPTED', '0', 'Первый текст про усиленную работу над этим проектом', '2020-01-06 17:21:12', 'Этот проект', '0', '2'), 
('2', '1', 'ACCEPTED', '0', 'Текст поста про собачек', '2020-03-06 17:21:12', 'Собачки ВУФ', '0', '1'), 
('3', '1', 'NEW', '0', 'Текст поста про кошечек', '2020-04-06 17:21:12', 'Кошечки котятки', '0', '1'), 
('4', '0', 'ACCEPTED', '0', 'Текст скрытого поста для проекта', '2020-05-06 17:21:12', 'Скрытый пост', '0', '2');

INSERT INTO `blog`.`tags` (`id`, `name`) 
VALUES ('1', 'проект'), 
('2', 'зверята'), 
('3', 'кошечки'), 
('4', 'собачки');

INSERT INTO `blog`.`tag2post` (`tag_id`, `post_id`) 
VALUES ('1', '1'), ('1', '4'), ('2', '2'), ('2', '3'), ('3', '2'), ('4', '3');