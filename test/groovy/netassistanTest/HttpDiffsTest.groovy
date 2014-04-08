package netassistanTest

import info.tss.netassistant.store.structure.Diff
import info.tss.netassistant.ui.ViewHelper

/**
 * Tests smart html diffs
 *
 * Original library test here: https://neil.fraser.name/software/diff_match_patch/svn/trunk/demos/demo_diff.html
 *
 * Author: tss
 * Date: 24.03.2014
 */
public class HttpDiffsTest extends GroovyTestCase {

    public void testPravAnonses(){
        String p = """ <div style="width:640px;float:left;">
 <div class="zg" style="border-top:5px solid #C7C600;border-bottom:1px dotted #C7C600;">
  АНОНСЫ БЕЛОРУССКОЙ ПРАВОСЛАВНОЙ ЦЕРКВИ
 </div>
 <div style="float:left;width:640px; margin:0 25px 0 0px;">
  <br />
  <br />
  <div class="pagination">
   <ul>
    <li class="currentpage">&nbsp;&nbsp;1&nbsp;&nbsp;</li>
   </ul>
  </div>
 </div>
</div> """
        String c = """ <html>
 <head></head>
 <body>
  АНОНСЫ БЕЛОРУССКОЙ ПРАВОСЛАВНОЙ ЦЕРКВИ
  <br />
  <br />
  <ul>
   <li>&nbsp;&nbsp;1&nbsp;&nbsp;</li>
  </ul>
 </body>
</html> """
        Diff d = ViewHelper.getColorizedHtml(p,c);
		println d.fullText
    }


    public void testKatehizator(){
        String p = """<div class="blog">
 <div class="items-leading">
  <div class="leading-0">
   <div class="page-header">
    <h2> <a href="/bulletin-board/105-vstrechi-s-vitaliem-pitanovym.html"> ВСТРЕЧИ С ВИТАЛИЕМ ПИТАНОВЫМ </a> </h2>
   </div>
   <div style="text-align: justify;">
    <span style="font-family: arial, helvetica, sans-serif; font-size: medium;">МИССИОНЕРСКИЙ ЦЕНТР &laquo;СТАВРОС&raquo; В МИНСКЕ: </span>
    <br />
    <br />
    <span style="font-size: medium; font-family: arial, helvetica, sans-serif;"><img src="/images/photo/GSTVuxwsY9k.jpg" alt="GSTVuxwsY9k.jpg" style="float: left; margin: 5px;" />29 декабря в Свято-Духовом кафедральном соборе г. Минска силами братства в честь Иоанна Богослова была организована встреча с Виталием Юрьевичем ПИТАНОВЫМ, ведущим сотрудником Православного апологетического центра &laquo;СТАВРОС&raquo; (г. Санкт-Петербург).</span>
    <br />
    <br />
    <span style="font-size: medium; font-family: arial, helvetica, sans-serif;">В ходе встречи прошла презентация Центра &laquo;СТАВРОС&raquo; и одноименного межрегионального православного миссионерского общественного движения, а также сайта stavroskrest.ru. Визитная карточка Центра, размещенная на упомянутом сайте, сообщает: </span>
    <br />
    <br />
    <span style="font-size: medium; font-family: arial, helvetica, sans-serif;">&laquo;16 февраля 2010 года митрополит Санкт-Петербургский и Ладожский Владимир благословил создание Православного апологетического центра &laquo;Ставрос&raquo;. &laquo;Ставрос&raquo; по-гречески означает &laquo;крест&raquo;. Центр является миссионерско-апологетическим по своим задачам, при этом апологетическое служение является средством для миссионерской деятельности. Миссионерское и апологетическое служения происходят при личном общении с людьми, через чтение лекций, подготовку аудио, видео и текстовых материалов, раскрывающих суть православного мировоззрения в контексте конструктивной полемики с иноверием, инославием, сектами и секулярными идеологиями, уводящими людей от Христа и Его Церкви. Центр осуществляет деятельность, направленную на профилактику сектантства, а также оказывает консультационную помощь пострадавшим от деятельности сект. Одна из целей центра заключается в объединении усилий и оказании помощи в реализации проектов православных миссионеров и апологетов. Центр возглавляется советом, состоящим из священнослужителей и мирян Русской Православной Церкви&raquo;. Кстати сказать, Центр носит имя святителя Николая Японского.<br /><br /></span>
   </div>
   <p class="readmore"><a class="btn" href="/bulletin-board/105-vstrechi-s-vitaliem-pitanovym.html"> <span class="icon-chevron-right"></span> Подробнее... </a></p>
  </div>
  <div class="clearfix"></div>
 </div>
 <!-- end items-leading -->
 <div class="clearfix"></div>
 <div class="items-row cols-1 row-0 row-fluid">
  <div class="span12">
   <div class="item column-1">
    <div class="page-header">
     <h2> <a href="/bulletin-board/86-trebuetsya-pomoshch-v-ukrashenii-mesta-upokoeniya-svyatoj-blazhennoj-valentiny-minskoj.html"> Требуется помощь в украшении места упокоения Святой блаженной Валентины Минской </a> </h2>
    </div>
    <div style="text-align: justify;">
     <span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><img src="/images/photo/minskaya.jpg" alt="minskaya.jpg" width="229" height="307" style="float: left; margin: 5px;" />В предстоящую субботу, 12 октября, требуется помощь в украшении места упокоения Святой блаженной Валентины Минской к празднику Покрова Пресвятой Богородицы. Господь благословит за этот труд и поможет в различных направлениях жизни.<br /><br /></span>
    </div>
    <div style="text-align: justify;">
     <span style="font-family: arial, helvetica, sans-serif; font-size: medium;">Сбор будет проходить в 8.05–8.20 на Центральном железнодорожном вокзале возле касс пригородных поездов (правое крыло вокзала, если смотреть на вокзал со стороны ул. Кирова). Можно приезжать и в течение дня. Любая помощь будет важна. Расписание электричек, в таком случае, можно посмотреть на сайте rw.by.<br /><br /></span>
    </div>
    <div style="text-align: justify;">
     <span style="font-family: arial, helvetica, sans-serif; font-size: medium;">Одевайся по погоде. По возможности, возьми с собой или одеть рабочую одежду, взять перчатки для работы, и немного еды, термос с чаем или кофе. Стоимость билета в обе стороны около 5200 руб. Если есть возможность, зови друзей.<br /><br /></span>
    </div>
    <p class="readmore"><a class="btn" href="/bulletin-board/86-trebuetsya-pomoshch-v-ukrashenii-mesta-upokoeniya-svyatoj-blazhennoj-valentiny-minskoj.html"> <span class="icon-chevron-right"></span> Подробнее... </a></p>
   </div>
   <!-- end item -->
  </div>
  <!-- end spann -->
 </div>
 <!-- end row -->
 <div class="items-row cols-1 row-1 row-fluid">
  <div class="span12">
   <div class="item column-1">
    <div class="page-header">
     <h2> <a href="/bulletin-board/82-lyubov-v-brake-prosto-o-slozhnom.html"> Любовь в браке: просто о сложном</a> </h2>
    </div>
    <p style="text-align: justify;"><span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><img src="/images/photo/IMG_3642.jpg" alt="IMG_3642.jpg" width="98" height="150" style="float: left; margin: 5px;" />9 октября 2013 в 18:00 <br /><br />В Центре духовного просвещения и социального служения &laquo;Всех скорбящих Радость&raquo;&nbsp;</span><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">г. Минск, ул.Притыцкого, 65 кабинет № 136 воскресной школы), состоится встреча Клуба &laquo;Молодая семья&raquo; на тему:&nbsp;</span><span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><strong>&laquo;Любовь в браке: просто о сложном&raquo;.&nbsp;<br /><br /></strong></span><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">Приглашаются молодые семьи, семьи с педагогическим стажем, все, кто только еще мечтает о создании домашнего очага,&nbsp;</span><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">а главное – все, кто молод душой!</span><br /><span style="font-family: arial, helvetica, sans-serif; font-size: medium; line-height: 1.3em;"><br />Организаторы: приход в честь иконы Божией Матери &laquo;Всех скорбящих Радость&raquo;, община в честь святых мучеников младенцев Вифлеемских, волонтерский отряд &laquo;Элейсон&raquo;.</span><br /> <br /><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">Ведущие клуба – политолог Игорь Понятовский и семейный психолог Екатерина Понятовская.</span><br /> <br /><strong><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">контактный телефон +37525 611-24-27 Игорь</span></strong></p>
   </div>
   <!-- end item -->
  </div>
  <!-- end spann -->
 </div>
 <!-- end row -->
 <div class="items-row cols-1 row-2 row-fluid">
  <div class="span12">
   <div class="item column-1">
    <div class="page-header">
     <h2> <a href="/bulletin-board/73-da-budet-zhizn.html"> Да! Будет жизнь!</a> </h2>
    </div>
    <p><span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><img src="/images/photo/LOGO_Ladochka_corr.jpg" alt="LOGO_Ladochka_corr.jpg" width="200" style="float: left; margin: 5px;" />В рамках подготовки к Фестивалю социальной видеорекламы в защиту жизни и семьи &laquo;Ладошка&raquo; 28 сентября, в 11.00 в каб. № 136 Центра духовного просвещения и социального служения Белорусской Православной Церкви &laquo;Всех скорбящих Радость&raquo;, по адресу: г.Минск, ул.Притыцкого, 65 (ст. М &laquo;Спортивная&raquo;), будет проведен бесплатный мастер-класс по технологиям создания видеороликов социальной рекламы.<br /><br />Фестиваль социальной рекламы в защиту жизни и семьи &quot;ЛАДОШКА&quot;, состоится 16 ноября, в 11.00., в конференц-зале Центра духовного просвещения и социального служения Белорусской Православной Церкви &laquo;Всех скорбящих Радость&raquo; по адресу: г.Минск, ул.Притыцкого, 65 (ст. М &laquo;Спортивная&raquo;).</span><br /> <br /><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">Девиз фестиваля: &laquo;Да! Будет жизнь!&raquo; <br /><br /></span></p>
    <p class="readmore"><a class="btn" href="/bulletin-board/73-da-budet-zhizn.html"> <span class="icon-chevron-right"></span> Подробнее... </a></p>
   </div>
   <!-- end item -->
  </div>
  <!-- end spann -->
 </div>
 <!-- end row -->
 <div class="items-row cols-1 row-3 row-fluid">
  <div class="span12">
   <div class="item column-1">
    <div class="page-header">
     <h2> <a href="/bulletin-board/66-nabor-uchashchikhsya.html"> НАБОР УЧАЩИХСЯ</a> </h2>
    </div>
    <span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><img src="/images/photo/logo_katehizator.jpg" alt="2" width="150" height="150" style="float: left; margin: 5px;" />Школа катехизаторов Минской епархии объявляет набор учащихся на 2013-2014 учебный год на подготовительное отделение и на 1 курс.&nbsp;<br />Набор учащихся каждый понедельник, среду и пятницу в сентябре 2013 г. с 18.00 до 21.00. Для поступления требуется иметь устное благословение священника.&nbsp;<a href="/for-students/67-pravila-priema.html"><br /></a></span></span>
    <p><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">СПРАВКИ ПО ТЕЛЕФОНУ:<br /></span><br /><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">МТС 8-029-755-59-64 – в любое время</span><br /><span style="font-family: arial, helvetica, sans-serif; font-size: medium;">городской 253-14-74 – в дни и часы приема<br /><br /><span>Правила приема доступны по</span><a href="/for-students/67-pravila-priema.html">&nbsp;ссылке.</a><br /></span></p>
    <span style="font-family: arial, helvetica, sans-serif; font-size: medium;"><a href="/for-students/67-pravila-priema.html"><br /></a></span>
   </div>
   <!-- end item -->
  </div>
  <!-- end spann -->
 </div>
 <!-- end row -->
</div>
"""
        String c = """<html>
 <head></head>
 <body>
  <a href="/bulletin-board/114-bibliya-kak-pamyatnik-kultury.html"> БИБЛИЯ КАК ПАМЯТНИК КУЛЬТУРЫ</a>
  <p><img alt="photo/biblio .jpg" width="349" height="239" />Издательство &laquo;ЗОРНЫ ВЕРАСЕНЬ&raquo;<br />Предпринимает второе издание книги<br />БИБЛИЯ КАК ПАМЯТНИК КУЛЬТУРЫ<br />Авторы: С.В. Мандрик, А.О. Горанский</p>
  <p>Эта книга рекомендована учреждением образования &laquo;Республиканский институт профессионального образования&raquo; Министерства образования Республики Беларусь в качестве пособия для учащихся учреждений образования, реализующих образовательные программы профессионально-технического и среднего специального образования.<br /><br /></p>
  <p><a href="/bulletin-board/114-bibliya-kak-pamyatnik-kultury.html"> Подробнее... </a></p>
  <a href="/bulletin-board/105-vstrechi-s-vitaliem-pitanovym.html"> ВСТРЕЧИ С ВИТАЛИЕМ ПИТАНОВЫМ </a> МИССИОНЕРСКИЙ ЦЕНТР &laquo;СТАВРОС&raquo; В МИНСКЕ:
  <br />
  <br />
  <img alt="GSTVuxwsY9k.jpg" />29 декабря в Свято-Духовом кафедральном соборе г. Минска силами братства в честь Иоанна Богослова была организована встреча с Виталием Юрьевичем ПИТАНОВЫМ, ведущим сотрудником Православного апологетического центра &laquo;СТАВРОС&raquo; (г. Санкт-Петербург).
  <br />
  <br /> В ходе встречи прошла презентация Центра &laquo;СТАВРОС&raquo; и одноименного межрегионального православного миссионерского общественного движения, а также сайта stavroskrest.ru. Визитная карточка Центра, размещенная на упомянутом сайте, сообщает:
  <br />
  <br /> &laquo;16 февраля 2010 года митрополит Санкт-Петербургский и Ладожский Владимир благословил создание Православного апологетического центра &laquo;Ставрос&raquo;. &laquo;Ставрос&raquo; по-гречески означает &laquo;крест&raquo;. Центр является миссионерско-апологетическим по своим задачам, при этом апологетическое служение является средством для миссионерской деятельности. Миссионерское и апологетическое служения происходят при личном общении с людьми, через чтение лекций, подготовку аудио, видео и текстовых материалов, раскрывающих суть православного мировоззрения в контексте конструктивной полемики с иноверием, инославием, сектами и секулярными идеологиями, уводящими людей от Христа и Его Церкви. Центр осуществляет деятельность, направленную на профилактику сектантства, а также оказывает консультационную помощь пострадавшим от деятельности сект. Одна из целей центра заключается в объединении усилий и оказании помощи в реализации проектов православных миссионеров и апологетов. Центр возглавляется советом, состоящим из священнослужителей и мирян Русской Православной Церкви&raquo;. Кстати сказать, Центр носит имя святителя Николая Японского.
  <br />
  <br />
  <p><a href="/bulletin-board/105-vstrechi-s-vitaliem-pitanovym.html"> Подробнее... </a></p>
  <a href="/bulletin-board/86-trebuetsya-pomoshch-v-ukrashenii-mesta-upokoeniya-svyatoj-blazhennoj-valentiny-minskoj.html"> Требуется помощь в украшении места упокоения Святой блаженной Валентины Минской </a>
  <img alt="minskaya.jpg" width="229" height="307" />В предстоящую субботу, 12 октября, требуется помощь в украшении места упокоения Святой блаженной Валентины Минской к празднику Покрова Пресвятой Богородицы. Господь благословит за этот труд и поможет в различных направлениях жизни.
  <br />
  <br /> Сбор будет проходить в 8.05–8.20 на Центральном железнодорожном вокзале возле касс пригородных поездов (правое крыло вокзала, если смотреть на вокзал со стороны ул. Кирова). Можно приезжать и в течение дня. Любая помощь будет важна. Расписание электричек, в таком случае, можно посмотреть на сайте rw.by.
  <br />
  <br /> Одевайся по погоде. По возможности, возьми с собой или одеть рабочую одежду, взять перчатки для работы, и немного еды, термос с чаем или кофе. Стоимость билета в обе стороны около 5200 руб. Если есть возможность, зови друзей.
  <br />
  <br />
  <p><a href="/bulletin-board/86-trebuetsya-pomoshch-v-ukrashenii-mesta-upokoeniya-svyatoj-blazhennoj-valentiny-minskoj.html"> Подробнее... </a></p>
  <a href="/bulletin-board/82-lyubov-v-brake-prosto-o-slozhnom.html"> Любовь в браке: просто о сложном</a>
  <p><img alt="IMG_3642.jpg" width="98" height="150" />9 октября 2013 в 18:00 <br /><br />В Центре духовного просвещения и социального служения &laquo;Всех скорбящих Радость&raquo;&nbsp;г. Минск, ул.Притыцкого, 65 кабинет № 136 воскресной школы), состоится встреча Клуба &laquo;Молодая семья&raquo; на тему:&nbsp;<strong>&laquo;Любовь в браке: просто о сложном&raquo;.&nbsp;<br /><br /></strong>Приглашаются молодые семьи, семьи с педагогическим стажем, все, кто только еще мечтает о создании домашнего очага,&nbsp;а главное – все, кто молод душой!<br /><br />Организаторы: приход в честь иконы Божией Матери &laquo;Всех скорбящих Радость&raquo;, община в честь святых мучеников младенцев Вифлеемских, волонтерский отряд &laquo;Элейсон&raquo;.<br /> <br />Ведущие клуба – политолог Игорь Понятовский и семейный психолог Екатерина Понятовская.<br /> <br /><strong>контактный телефон +37525 611-24-27 Игорь</strong></p>
  <a href="/bulletin-board/73-da-budet-zhizn.html"> Да! Будет жизнь!</a>
  <p><img alt="LOGO_Ladochka_corr.jpg" width="200" />В рамках подготовки к Фестивалю социальной видеорекламы в защиту жизни и семьи &laquo;Ладошка&raquo; 28 сентября, в 11.00 в каб. № 136 Центра духовного просвещения и социального служения Белорусской Православной Церкви &laquo;Всех скорбящих Радость&raquo;, по адресу: г.Минск, ул.Притыцкого, 65 (ст. М &laquo;Спортивная&raquo;), будет проведен бесплатный мастер-класс по технологиям создания видеороликов социальной рекламы.<br /><br />Фестиваль социальной рекламы в защиту жизни и семьи &quot;ЛАДОШКА&quot;, состоится 16 ноября, в 11.00., в конференц-зале Центра духовного просвещения и социального служения Белорусской Православной Церкви &laquo;Всех скорбящих Радость&raquo; по адресу: г.Минск, ул.Притыцкого, 65 (ст. М &laquo;Спортивная&raquo;).<br /> <br />Девиз фестиваля: &laquo;Да! Будет жизнь!&raquo; <br /><br /></p>
  <p><a href="/bulletin-board/73-da-budet-zhizn.html"> Подробнее... </a></p>
  <ol>
   <li> <a href="/bulletin-board/66-nabor-uchashchikhsya.html"> НАБОР УЧАЩИХСЯ</a> </li>
  </ol>
  <p> Страница 1 из 2 </p>
  <ul>
   <li><a><i></i></a></li>
   <li><a><i></i></a></li>
   <li><a>1</a></li>
   <li><a href="/bulletin-board.html?start=5">2</a></li>
   <li><a href="/bulletin-board.html?start=5"><i></i></a></li>
   <li><a href="/bulletin-board.html?start=5"><i></i></a></li>
  </ul>
 </body>
</html>
"""
        Diff d = ViewHelper.getColorizedHtml(p,c);
        println d.fullText
    }







}
