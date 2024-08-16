INSERT INTO `picple`.`user` (`email`, `password`, `nickname`, `created_at`, `updated_at`, `is_deleted`, `refresh_token`) VALUES 
('rladpgns@gmail.com', '$2a$10$Im0jUc.Z0UElstRqPV2noO3z4rSshHEwtn66Wo5P21v43zkC1yb4m', '김예훈', '2024-08-01 13:36:09', '2024-08-01 13:36:09', 0),
('rlaguswo@gmail.com', '$2a$10$Im0jUc.Z0UElstRqPV2noO3z4rSshHEwtn66Wo5P21v43dngjeQm1', '김현재', '2024-08-01 13:37:09', '2024-08-01 13:37:09', 0),
('dbqudwn@gmail.com', '$2a$10$Im0jUc.Z0UElstRqPV2noO3z4rSshHEwtn66Wo5P21v43snvkd40a', '유병주', '2024-08-01 13:37:21', '2024-08-01 13:37:21', 0),
('dhrwlstjr@gmail.com', '$2a$10$Im0jUc.Z0UElstRqPV2noO3z4rSshHEwtn66Wo5P21v43dmbkqi31', '옥진석', '2024-08-01 13:37:42', '2024-08-01 13:37:42', 0),
('duarbdud@gmail.com', '$2a$10$Im0jUc.Z0UElstRqPV2noO3z4rSshHEwtn66Wo5P21v43dnbC10fC', '염규영', '2024-08-01 13:38:15', '2024-08-01 13:38:15', 0),
('qkrtjdgns@gmail.com', '$2a$10$Im0jUc.Z0UElstRqPV2noO3z4rSshHEwtn66Wo5P21v43ak0A1ngv', '박성훈', '2024-08-01 13:38:46', '2024-08-01 13:38:46', 1);

INSERT INTO `picple`.`photo` (`photo_url`, `is_shared`, `created_at`, `is_deleted`) VALUES 
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-16-34a3b936-4e80-4d6e-8b75-d2280f5fd16f.jpg', 0, '2024-08-06 13:36:09', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-16-aecbc404-993e-4041-8ce9-b6e7007a5f5e.jpg', 0, '2024-08-06 16:14:45', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-16-45d31bb5-9f47-4668-9b57-531fc3504579.jpg', 0, '2024-08-07 18:32:10', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-16-bd5f2054-0bda-499e-b11f-f2df0beafe3e.jpg', 0, '2024-08-08 11:22:19', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-16-1466bca9-df80-4ef9-93c4-aff631ec9626.jpg', 0, '2024-08-08 17:33:29', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-15-497ec4f8-a050-4be6-8c9d-f61fb11cbce0.jpg', 0, '2024-08-11 10:11:36', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-16-b9c5adcb-5554-45f3-9c5c-a72e5f9eed0d.jpg', 0, '2024-08-11 13:53:11', 0),
('https://picple.s3.ap-northeast-2.amazonaws.com/2024-08-14-dc6dce66-7d31-4e73-9ea3-a926c1dd4020.jpg', 0, '2024-08-12 15:13:56', 0);

INSERT INTO `picple`.`photo_user` (`photo_id`, `user_id`, `content`) VALUES 
(1, 2, '재미로 찍은 사진'),
(2, 2, '재밌는 배경으로 찍은 사진'),
(3, 3, '연예인과 함께'),
(4, 1, ''),
(5, 2, ''),
(6, 1, ''),
(7, 3, ''),
(8, 1, '언제 찍은거지');

INSERT INTO background (background_title, created_at, is_deleted) VALUES
('ai/091a7f06-4316-43b6-9ea9-4a9323f05ad2.png', '2024-08-11 13:36:09', 0),
('ai/e37d646e-d528-43fd-b075-ef54de447133.png', '2024-08-11 14:11:12', 0),
('ai/091a7f06-4316-43b6-9ea9-4a9323f05ad2.png', '2024-08-11 17:23:34', 0),
('ai/c4de9d40-157b-409f-9cb6-af175938b39d.png', '2024-08-12 11:31:41', 0),
('ai/6f47b9da-ff55-4d8b-aa15-18102f71af2e.png', '2024-08-12 16:28:23', 0),
('ai/079ed936-8224-48f8-93d0-aa99a1308fe5.png', '2024-08-12 22:43:51', 0),
('ai/59fa11bd-d44e-4129-9cba-2aaca32a3584.png', '2024-08-13 12:18:10', 0),
('ai/64806c33-8e5b-487a-8cbc-93f96c05d49c.png', '2024-08-14 10:09:02', 0),
('ai/24fe3188-62fc-48bc-ac23-d9616e50268e.png', '2024-08-14 18:35:53', 0),
('ai/1654502e-9679-43da-839e-b625ab7d8ae0.png', '2024-08-15 21:45:23', 0);

INSERT INTO background_user (background_id, user_id) VALUES
(1, 1),
(2, 3),
(3, 2),
(4, 4),
(5, 1),
(6, 5),
(7, 3),
(8, 4),
(9, 2),
(10, 3);

INSERT INTO calendar (photo_id, user_id, created_at) VALUES
(1, 1, '2024-08-06 13:37:09'),
(2, 2, '2024-08-06 16:15:45'),
(3, 3, '2024-08-07 18:33:10'),
(4, 1, '2024-08-08 11:23:19'),
(5, 2, '2024-08-08 17:34:29'),
(6, 1, '2024-08-11 10:12:36'),
(7, 3, '2024-08-11 13:54:11'),
(8, 1, '2024-08-12 15:14:56');

INSERT INTO board (photo_id, user_id, hit, created_at, is_deleted) VALUES
(1, 1, 4, '2024-08-06 13:47:09', 0),
(2, 2, 2, '2024-08-06 16:25:45', 0),
(3, 3, 3, '2024-08-07 18:43:10', 0),
(4, 1, 0, '2024-08-08 11:33:19', 0),
(5, 2, 1, '2024-08-08 17:44:29', 0),
(6, 1, 0, '2024-08-11 10:22:36', 0);

INSERT INTO `boardlike` (user_id, board_id, is_liked) VALUES
(1, 1, 1),
(2, 1, 1),
(1, 2, 1),
(3, 3, 1),
(4, 1, 1),
(5, 5, 1),
(3, 1, 1),
(4, 2, 1),
(2, 3, 1),
(5, 3, 1);