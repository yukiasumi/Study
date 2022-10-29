select A.sname '姓名',B.score '语文',C.score '数学',D.score '英语'

from student A,sc B,sc C,sc D 

where A.id=B.sid and B.cid in(select id from course where cname='语文')

and A.id=C.sid and C.cid in(select id from course where cname='数学')

and A.id=D.sid and D.cid in(select id from course where cname='英语');



