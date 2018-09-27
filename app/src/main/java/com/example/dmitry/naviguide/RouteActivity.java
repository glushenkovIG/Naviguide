package com.example.dmitry.naviguide;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmitry.naviguide.adapters.SuperAdapter;
import com.example.dmitry.naviguide.auxiliary.GeometryHelper;
import com.example.dmitry.naviguide.auxiliary.Site;
import com.example.dmitry.naviguide.auxiliary.SuperRecyclerView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RouteActivity extends AppCompatActivity {
    private SectionsPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    Route route;
    MapFragment mapFragment;
    DescrFragment descrFragment;
    SitesFragment sitesFragment;
    private ExecutorService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        route = RoutesSingletone.getInstance().getRoutes().get(getIntent().getIntExtra("route_index", 0));
        mapFragment = new MapFragment();
        descrFragment = new DescrFragment();
        sitesFragment = new SitesFragment();

        service = Executors.newCachedThreadPool();

//        service.execute(new Runnable() {
//            @Override
//            public void run() {
//                sites = new HashMap<>();
//
//                sites.put("Древняя Москва", new Site[]{
//                        new Site("Алтуфьево (усадьба)", "Уса́дьба Алту́фьево — комплекс памятников усадебной архитектуры XVIII—XIX веков на северо-востоке Москвы в районе Лианозово", /*lat*/ 55.907780000000002, /*lon*/ 37.585830000000001),
//                        new Site("Аршиновский парк", "Аршиновский парк — сосновый парк, расположенный между Бакинской улицей и улицей Бехтерева в районе Царицыно.", /*lat*/ 55.620559999999998, /*lon*/ 37.654170000000001),
//                        new Site("Библио-Глобус", "«Библио-Глобус» (в 1957—1992 годы — магазин № 120 «Книжный мир») — один из самых крупных книжных магазинов в России, расположенный в историческом центре Москвы. В книжном магазине также располагается одноименный офис туроператора, для частных лиц, с 1994 года.", /*lat*/ 55.7599041, /*lon*/ 37.630258499999997),
//                        new Site("Гараж Госплана", "Гара́ж Госпла́на — здание гаража на Авиамоторной улице в Москве, построенное в 1936 году. Выполнено по проекту архитектора Константина Мельникова в соавторстве с В. И. Курочкиным, предназначался для автомобилей cотрудников Госплана СССР. Является памятником архитектуры советского авангарда, круглое окно должно ассоциироваться с фарой", /*lat*/ 55.739488899999998, /*lon*/ 37.719713900000002),
//                        new Site("Гараж Интуриста", "Гара́ж «Интури́ста» — многоэтажное здание гаража, расположенное в Москве в районе Марьина роща. Строительство началось в 1932 году, а завершилось, по данным Музея архитектуры имени Щусева, в 1934-м. Издательский дом «Коммерсантъ» годом окончания строительства называет 1938-й. Здание выполнено по проекту архитекторов Константина Мельникова и Валерия Ивановича Курочкина по заказу Внешнеэкономического акционерного общества по туризму и инвестициям «Интурист»", /*lat*/ 55.793694000000002, /*lon*/ 37.606527999999997),
//                        new Site("Гараж на Новорязанской улице", "Гара́ж на Новоряза́нской у́лице — памятник архитектуры советского авангарда, построен на Новорязанской улице в Москве по проекту архитектора Константина Мельникова и инженера Владимира Шухова в 1926—1929 годах как гараж для грузовых машин. Является памятником архитектуры регионального значения", /*lat*/ 55.772219999999997, /*lon*/ 37.66583),
//                        new Site("Главный ботанический сад имени Н. В. Цицина РАН", "Главный ботанический сад имени Н. В. Ци́цина РАН (Москва) — крупнейший ботанический сад Европы, располагает богатейшими коллекциями растений, представляющих разнообразный растительный мир практически всех континентов и климатических зон земного шара. Основан 14 апреля 1945 года, первым директором стал Николай Васильевич Цицин. Живые коллекции насчитывают 8220 видов и 8110 форм и сортов растений — всего 16 330 таксонов. На основе коллекций с использованием современных приёмов ландшафтной архитектуры созданы ботанические экспозиции растений: природной флоры России и бывшего СССР, дендрарий, экспозиция тропических и субтропических растений, цветочно-декоративных и культурных растений.", /*lat*/ 55.839199999999998, /*lon*/ 37.6008),
//                        new Site("Городок художников", "Городо́к худо́жников — архитектурный комплекс, объект культурного наследия (памятник истории и культуры) на северо-западе Москвы, на территории района Аэропорт Северного административного округа.", /*lat*/ 55.791939999999997, /*lon*/ 37.566389999999998),
//                        new Site("Городская усадьба Г. П. Юргенсона", "Городска́я уса́дьба Г. П. Юргенсона — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.75667, /*lon*/ 37.641939999999998),
//                        new Site("Городская усадьба Гончарова — Филипповых", "Городская усадьба А. А. Гончарова — Филипповых — памятник архитектуры, расположенный в Москве по адресу Яузская улица, дом 1/15.", /*lat*/ 55.750230600000002, /*lon*/ 37.643994399999997),
//                        new Site("Городская усадьба П. П. Игнатьевой – Н. А. Белкина", "Городска́я уса́дьба П. П. Игна́тьевой — Н. А. Бе́лкина — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.725830000000002, /*lon*/ 37.621670000000002)
//                });
//
//                sites.put("Культурная Москва", new Site[]{
//                        new Site("Городская усадьба Я. А. Маслова — А. П. Оболенского", "Городская усадьба Я. А. Маслова — А. П. Оболенского — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.763891940000001, /*lon*/ 37.624068059999999),
//                        new Site("Грибоедовский ЗАГС", "Грибое́довский ЗАГС (''Дворец бракосочетания № 1'') — первый Московский Дворец бракосочетания, открытый в 1961 году, в бывшем особняке, построенном в 1909 году по заказу купца А. В. Рериха архитектором С. В. Воскресенским. Расположен в центре старой Москвы.", /*lat*/ 55.765805999999998, /*lon*/ 37.644694000000001),
//                        new Site("Дача Муромцева", "Да́ча Му́ромцева — общее название исторической дачи Муромцева, дачи председателя первой Государственной думы Сергея Муромцева в Царицыне (Москва), возведённой в 1893 году и разобранной в середине 1960-х годов; а также здания более поздней постройки — деревянного дома, возведённого в 1960-х на фундаменте первоначального строения и получившего известность в конце 2000-х годов под аналогичным названием «Дача Муромцева».", /*lat*/ 55.608713899999998, /*lon*/ 37.668105599999997),
//                        new Site("Детский мир (Лубянская площадь)", "«Де́тский мир» (с 2015 года — «Центральный детский магазин на Лубянке») — универмаг с товарами для детей и юношества, построенный в 1953—1957 годах в центре Москвы, на площади Дзержинского (с 1990 года — Лубянская площадь) по проекту архитектора Алексея Душкина (соавторы И. М. Потрубач и Г. Г. Аквилев, инженер Л. М. Глиэр) на месте снесённого Лубянского пассажа, над станцией метро глубокого заложения «Дзержинская» (с 1990 года — «Лубянка»).", /*lat*/ 55.760150000000003, /*lon*/ 37.624811100000002),
//                        new Site("Дом архитекторов (Москва)", "Дом архитекторов (также Дом на Ростовской набережной) — жилой дом, расположенный в Москве на Ростовской набережной, 5. Построен архитектором Щусевым в стиле постконструктивизма в 1930-х годах. Здание является частью нереализованного проекта парадного ансамбля Смоленской и Ростовской набережных, задуманного Щусевым. Боковые крылья, облицованные бежевым кирпичом, достроены в начале 1960-х.", /*lat*/ 55.7425, /*lon*/ 37.575560000000003),
//                        new Site("Дом банка и торгового дома «И. В. Юнкер и Ко»", "Дом банка и торгового дома «И. В. Юнкер и Ко» — историческое здание, расположено в Москве на улице Кузнецкий Мост, 16/5. Построено в 1876 году архитектором П. С. Кампиони. Перестроено в 1900—1908 годах в стиле модерн архитектором А. Э. Эрихсоном. В 1913—1915 годах архитектором В. И. Ерамишанцевым, при участии братьев Весниных фасад здания перестроен в неоклассических формах. Дом банка и торгового дома «И. В. Юнкер и Ко» является объектом культурного наследия регионального значения. По мнению ряда искусствоведов, здание является одной из лучших построек московской неоклассики.", /*lat*/ 55.761766110000003, /*lon*/ 37.622118059999998),
//                        new Site("Дом Дворцового ведомства в Денежном переулке", "Дом Дворцо́вого ве́домства в Де́нежном переу́лке — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.742220000000003, /*lon*/ 37.588059999999999),
//                        new Site("Дом И. Л. Чернышёва", "Городская усадьба И. Л. Чернышёва — памятник архитектуры, расположенный в Москве.", /*lat*/ 55.766390000000001, /*lon*/ 37.665280000000003),
//                        new Site("Дом культуры имени Зуева", "Дом культуры имени Зуева (или Клуб профсоюзов коммунальников имени Зуева) — здание дома культуры в Москве, расположенное на Лесной улице; один из наиболее ярких и известных в мире памятников конструктивизма. Построен в 1927—1929 годах по проекту архитектора Ильи Голосова и назван в честь участника революции 1905 года, слесаря трамвайного парка Сергея Зуева. На территории ДК работают театры, среди которых «Квартет И», «Другой театр».", /*lat*/ 55.779170000000001, /*lon*/ 37.590000000000003),
//                        new Site("Дом культуры имени Русакова", "Дом культу́ры и́мени И. В. Русако́ва (изначально — ''Клуб Русако́ва Сою́за Коммуна́льников'') — здание рабочего клуба на пересечении улиц Стромынка и Бабаевская в Москве. Было построено в 1929 году для работников Союза коммунальников по проекту архитектора Константина Мельникова. Является объектом культурного наследия России и входит в перечень Всемирного фонда памятников архитектуры", /*lat*/ 55.79139, /*lon*/ 37.687220000000003),
//                    });
//
//                sites.put("Шоппинг в Москве", new Site[]{
//                        new Site("Дом Мусорина-Полежаевой", "Дом Мусорина-Полежаевой — памятник архитектуры, расположен в Замоскворечье в Москве по адресу: ул. Бахрушина, д. 21, стр. 4.", /*lat*/ 55.734029999999997, /*lon*/ 37.636536110000002),
//                        new Site("Дом на Моховой", "Дом на Моховой — здание в центре Москвы на Моховой улице. Дом построен в 1934 году по проекту архитектора Ивана Жолтовского для сотрудников Моссовета. Строительство здания вызвало противоречивые отзывы и активную дискуссию среди архитекторов. Дом Жолтовского ознаменовал собой перелом в советской архитектуре, его прозвали «''гвоздём в гроб конструктивизма''». Здание имеет статус объекта культурного наследия регионального значения. С 2000-х годов в нём размещается головной офис компании АФК «Система».", /*lat*/ 55.75611, /*lon*/ 37.613059999999997),
//                        new Site("Дом Общества «Детский труд и отдых»", "Дом Общества „Детский труд и отдых“ — памятник архитектуры, расположенный в Москве.", /*lat*/ 55.790559999999999, /*lon*/ 37.598610000000001),
//                        new Site("Дом Орловых-Мещерских", "Дом Орловых-Мещерских — достопримечательность Москвы, образец классической архитектуры. Включен М. Ф. Казаковым в альбом лучших «партикулярных» зданий города. Расположен на углу Романова переулка (дом 7) и Большой Никитской (дом 5/7).", /*lat*/ 55.755389000000001, /*lon*/ 37.609000000000002),
//                        new Site("Дом П. А. Сырейщикова (Рахмановых)", "Дом П. А. Сырейщикова (Рахмановых) — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.754440000000002, /*lon*/ 37.654440000000001),
//                        new Site("Дом Палибина", "Дом Пали́бина — памятник архитектуры, расположенный в Москве.", /*lat*/ 55.738610000000001, /*lon*/ 37.57694),
//                        new Site("Дом писателей в Лаврушинском переулке", "Дом писателей в Лаврушинском переулке — жилой дом—памятник архитектуры, расположенный в городе Москве, один из объектов культурного наследия федерального значения.", /*lat*/ 55.741109999999999, /*lon*/ 37.621940000000002),
//                        new Site("Дом Пороховщикова", "Дом Пороховщико́ва — особняк в центре Москвы, по адресу: Староконюшенный переулок, д. 36. Построен в 1871—1872 годах для российского предпринимателя и мецената А. А. Пороховщикова, владельца гостиницы «Славянский базар» и одноимённого ресторана. Здание, построенное на древнем фундаменте из дерева, удачно синтезировало приёмы национальной архитектурной традиции. Сложенный из толстых брёвен, украшенный резными наличниками, карнизами и подзорами, особняк сочетает крупные объёмы и не лишённый живописности облик. Проект дома в 1873 году получил премию на Всемирной выставке в Вене. Объект культурного наследия федерального значения.", /*lat*/ 55.749813000000003, /*lon*/ 37.594641000000003),
//                        new Site("Дом поэта А. Н. Майкова", "Дом поэта А. Н. Майкова — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.774999999999999, /*lon*/ 37.640560000000001),
//                });
//
//                sites.put("Ночная Москва", new Site[]{
//                        new Site("Доходный дом Б. Н. Шнауберта", "Доходный дом Б. Н. Шнауберта — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.755279999999999, /*lon*/ 37.641109999999998),
//                        new Site("Доходный дом Бебутовой", "Доходный дом Бебутовой — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.766939999999998, /*lon*/ 37.62556),
//                        new Site("Доходный дом В. И. Фирсановой", "Доходный дом В. И. Фирсановой — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.76417, /*lon*/ 37.620829999999998),
//                        new Site("Доходный дом Е. А. Депре", "Дохо́дный дом Е. А. Депре́ — памятник архитектуры, расположенный в городе Москве. В стенах строения жил драматург Натан Зархи.", /*lat*/ 55.768329999999999, /*lon*/ 37.616669999999999),
//                        new Site("Доходный дом купца Камзолкина", "Доходный дом купца Камзолкина — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.773330000000001, /*lon*/ 37.63167),
//                        new Site("Доходный дом купцов Архангельских", "Доходный дом купцов Архангельских — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.761670000000002, /*lon*/ 37.63861),
//                        new Site("Доходный дом Н. Г. Тарховой", "Доходный дом Н. Г. Тарховой — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.75694, /*lon*/ 37.651389999999999),
//                        new Site("Доходный дом Первого Российского страхового общества (Москва)", "Доходный дом Первого Российского страхового общества — исторический доходный дом в Москве, расположен на углу улиц Кузнецкий Мост и Большой Лубянки. Здесь в 1918—1952 годах размещался Народный комиссариат по иностранным делам (с 1946 года — Министерство иностранных дел СССР). Здание является выявленным объектом культурного наследия.", /*lat*/ 55.762386100000001, /*lon*/ 37.627591700000004),
//                        new Site("Доходный дом Сокол", "Доходный дом М. В. Сокол — исторический доходный дом в Москве, расположен на улице Кузнецкий Мост. Здание является объектом культурного наследия регионального значения.", /*lat*/ 55.761658300000001, /*lon*/ 37.616833300000003),
//                        new Site("Доходный дом Торлецкого — Захарьина", "", /*lat*/ 55.761416699999998, /*lon*/ 37.623702799999997),
//                        new Site("Доходный дом Трындиных", "Дохо́дный дом Тры́ндиных — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.755279999999999, /*lon*/ 37.641109999999998),
//                        new Site("Доходный дом Хомякова", "Доходный дом А. С. Хомякова — историческое здание в Москве на пересечении улиц Кузнецкий Мост и Петровка. Построен в 1900 году архитектором И. А. Ивановым-Шицем. Здание является редким сохранившимся памятником, выполненным в Москве в стилистике венского модерна.", /*lat*/ 55.761255599999998, /*lon*/ 37.618083300000002),
//                  });
//
//                sites.put("Парки Москвы", new Site[]{
//                        new Site("Московский газовый завод", "Московский газовый завод — бывший газовый завод в Басманном районе Центрального административного округа Москвы, построенный в 1865 году для освещения города, памятник промышленной архитектуры. В настоящее время помещения завода переоборудованы в бизнес-центр.", /*lat*/ 55.761110000000002, /*lon*/ 37.663060000000002),
//                        new Site("Московский планетарий", "Моско́вский планета́рий — один из самых больших в мире и самый старый планетарий в России. Расположен в Москве возле новой территории Московского зоопарка, недалеко от Садового кольца. Построен в 1927—1929 годах по проекту архитекторов М. О. Барща, М. И. Синявского и инженера Г. А. Зундблата.", /*lat*/ 55.761389999999999, /*lon*/ 37.58361),
//                        new Site("Надвратная церковь Страстного монастыря", "Святые ворота Страстного монастыря — въездные центральные ворота не существующего ныне Страстного монастыря в Москве, над которыми была надстроена церковь Алексия Человека Божия. Представляли собой доминирующий архитектурный акцент Страстной площади (ныне Пушкинской).", /*lat*/ 55.765243060000003, /*lon*/ 37.605328890000003),
//                        new Site("Новодевичье кладбище", "Новоде́вичье кла́дбище — кладбище в московских Хамовниках при Новодевичьем монастыре. Основано при Новодевичьем монастыре, построенном в 1525 году. Официально возраст кладбища отсчитывается с 1904 года.", /*lat*/ 55.724719999999998, /*lon*/ 37.554169999999999),
//                        new Site("Океанариум на Дмитровском шоссе", "Океанариум на Дмитровском шоссе — первый океанариум в Москве, располагающийся в торгово-развлекательном центре «РИО» на Дмитровском шоссе, 163. Признан крупнейшим в России, после Москвариума. Создан на средства ГК «Ташир». Инвестиции в проект составили 18 млн долларов.", /*lat*/ 55.909399999999998, /*lon*/ 37.539700000000003),
//                        new Site("Особняк Н. В. Кузнецовой", "Особня́к Наде́жды Вику́ловны Кузнецо́вой — правый корпус усадебного комплекса промышленника Матвея Кузнецова, официально принадлежавшего его жене.", /*lat*/ 55.782218059999998, /*lon*/ 37.633266110000001),
//                        new Site("Павильон «Украина» на ВДНХ", "Павильон «Украина» (экспозиция «Земледелие») — 58-й павильон ВДНХ, построенный в 1950—1954 годах. До 1963 года носил название «Украинская ССР».", /*lat*/ 55.83278, /*lon*/ 37.626939999999998),
//                        new Site("Палаты в Кадашёвской слободе", "Пала́ты в Кадашёвской слободе́ — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.742780000000003, /*lon*/ 37.621110000000002),
//                        new Site("Палаты Гребенщиковых", "Палаты Гребенщиковых — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.741109999999999, /*lon*/ 37.659170000000003),
//                        new Site("Палаты князя Андрея Друцкого", "Палаты князя Андрея Друцкого — памятник архитектуры, расположенный в городе Москве.", /*lat*/ 55.748330000000003, /*lon*/ 37.597499999999997),
//                    });
//
//                sites.put("Мраморные пещеры Крыма", new Site[]{
//                        new Site("Вход в пещеры", " \n Крым — удивительно романтичное и живописное место: море, горы и даже степи здесь кажутся необыкновенно заманчивыми и сказочными. Но полуостров славится и своими необыкновенно красивыми пещерами. Одна из таких, самая крупная, называется Мраморной. \n Добраться до Мраморной пещеры не так уж и просто, но она, безусловно, стоит затраченных усилий и времени. Итак: из города Симферополя следуют троллейбусы под номерами 1, 51, 52 с конечной остановкой в селе Заречное. Расстояние между населенными пунктами порядка 18 км.", /*lat*/ 55.907780000000002, /*lon*/ 37.585830000000001),
//                        new Site("Обвальный зал", " \n Настоящая гордость и изюминка этой пещеры — Обвальный зал, или Зал Перестройки. Это самое крупное подобное место в Крыму и одно из крупнейших на всем континенте. Зал Перестройки растянулся аж на 100 метров, а высота его составляет целых 28 метров. Колоны, сталактиты, сталагмиты, корралитовые цветы и многое другое создают впечатление, что зал был оформлен какими-то неизвестными сказочными мастерами, прекрасно знавшими свое искусство.", /*lat*/ 55.907780000000002, /*lon*/ 37.585830000000001),
//                        new Site("Дворцовый зал", "Еще один красивейший зал пещеры — это Дворцовый, который украшают изящные колонны, именуемые Королем и Королевой. Есть и особое предложение для туристов — тур в Нижнюю галерею, который занимает порядка трех часов. Начинается он в Обвальном зале, и те, кто захочет предпринять это увлекательное путешествие, совершенно точно не пожалеют. В ходе экскурсии они смогут осмотреть Розовый зал с каменными розами, украшающими потолок, и скелетами древних животных. Самый впечатляющий зал Нижней галереи — это, конечно, Балконный зал, однако и зал Надежды, Люстровый и Русловый залы, Геликтитовый и зал с милым названием Шоколадка также никого равнодушными не оставят.", /*lat*/ 55.907780000000002, /*lon*/ 37.585830000000001)
//
//                });
//
//                sites.put("Центр Москвы", new Site[]{
//                        new Site("Спасская башня Московского Кремля", "Спасская башня была сооружена в 1491 году в период княжения Ивана III архитектором Пьетро Антонио Солари, о чём свидетельствуют белокаменные плиты с памятными надписями, установленные над въездными воротами башни. С внешней стороны башни надпись сделана на латинском языке, с внутренней — на русском: «В лето 6999 [1491] июля божией милостию сделана бысть сия стрельница повелением Иоанна Васильевича государя и самодержца всея Руси и великого князя Володимерского и Московского и Новгородского и Псковского и Тверского и Югорского и Вятского и Пермского и Болгарского и иных в 30 лето государств его, а делал Пётр Антоний Солярио от града Медиолана».", 55.752544, 37.621425 ),
//                        new Site("Собор Василия Блаженного", "Собор объединяет десять церквей (приделов), часть из которых освящены в честь святых, дни памяти которых пришлись на решающие бои за Казань[3]. Центральная церковь сооружена в честь Покрова Богородицы, вокруг которой группируются отдельные церкви в честь: Святой Троицы, Входа Господня в Иерусалим, Николы Великорецкого, Трёх Патриархов: Александра, Иоанна и Павла Нового, Григория Армянского, Киприана и Иустины, Александра Свирского и Варлаама Хутынского, размещённые на одном основании-подклете, и придел в честь Василия Блаженного[4][5], по имени которого храм получил второе, более известное название.", 55.752667, 37.623222),
//                        new Site("Александровский сад", "Алекса́ндровский сад — парк в Тверском районе Москвы, расположен вдоль западной Кремлёвской стены, тянется от площади Революции до Кремлёвской набережной[1]. Был основан в 1812 году. Парк площадью 10 гектаров состоит из трёх частей: Верхнего, Среднего и Нижнего садов. В нём находятся такие исторические объекты, как Кутафья башня Кремля, Итальянский грот, обелиск к 300-летию Дома Романовых и другие. Особое место занимают памятники, посвященные Отечественной войне 1812 года и Великой Отечественной войне[1].", 55.752345, 37.613701),
//                        new Site("Храм Христа Спасителя", "Хра́м Христа́ Спаси́теля — кафедральный собор Русской православной церкви, расположенный в Москве на улице Волхонке. Существующее сооружение, построенное в 1990-х годах, является воссозданием одноимённого храма, созданного в XIX веке[1].", 55.744592, 37.605614),
//                        new Site("Памятник Петру Первому", "Памятник Петру в техническом плане представляет собой уникальное инженерное сооружение. Несущий каркас монумента выполнен из нержавеющей стали, на него навешены бронзовые детали облицовки. Отдельно друг от друга собирались образующая пьедестал нижняя часть памятника, корабль и фигура Петра; последние в готовом виде монтировались на пьедестал. Ванты корабля выполнены из нержавеющей стали. Каждый из них сплетён из нескольких тросов и закреплён таким образом, что полностью исключена их подвижность. Паруса имеют внутри пространственный металлический каркас (для уменьшения веса), они изготовлены из меди методом выколотки.", 55.738611, 37.608333),
//                        new Site("Парк искусств «Музеон»", "Парк искусств «Музеон» — музей скульптуры в Москве под открытым небом, крупнейший в России. В парке установлено более 800 работ. В коллекции Музеона представлены монументы вождей 1930—50-х годов, памятники эпохи соцреализма и бюсты Героев Социалистического Труда, а также работы скульпторов-авангардистов. Они расположены вдоль аллеи и на газонах, доступ к которым не ограничен: посетители музея свободно гуляют среди скульптур, прикасаются к ним и фотографируются[3]. Парк искусств находится в нижней части поймы Москвы-реки, он ограничен Крымским валом, набережной Москвы-реки и Мароновским переулком[4]. Объединение ЦПКиО им. М. Горького и Музеона обсуждали с 2014 года[5] — в октябре 2015 года парк искусств стал составной частью Парка Горького[6].", 55.735554, 37.607916),
//                        new Site("Крымский мост", "Кры́мский мост — висячий мост через Москву-реку, расположен на трассе Садового кольца, соединяет Зубовский бульвар с улицей Крымский Вал. Построен в 1938 году в рамках Генерального плана реконструкции Москвы 1935 года по проекту архитектора Александра Власова и инженера Бориса Петровича Константинова[3][4][5]. Существует несколько версий происхождения названия моста: по соединяемым им Крымской площади и улице Крымский Вал; по названию древнего Крымского брода; по находившемуся в XVI веке неподалёку от него двору крымского хана[4][1][6]. В 2007 году Правительство Москвы включило мост в реестр охраняемых объектов культурного наследия столицы[6][7][8].", 55.733889, 37.598889),
//                        new Site("Парк Горького", "Центра́льный парк культу́ры и óтдыха и́мени Макси́ма Гóрького (сокращённо ЦПКиО или парк Горького) — московский парк культуры и отдыха, столичная рекреационная зона, одна из самых больших и популярных в городе[2][3].\n" +
//                                "Партерная часть парка появилась в 1923 году после организации на этой территории Всероссийской сельскохозяйственной выставки (ВСХВ), планировку которой от входа до Нескучного сада выполнил архитектор-авангардист Константин Мельников. ЦПКиО был открыт 12 августа 1928 года, в 1932-м парку присвоили имя писателя Максима Горького. В разное время проектировкой парка занимались Эль Лисицкий и Александр Власов. Арка главного входа возведена в 1955 году по проекту архитектора Георгия Щуко[4].", 55.731169, 37.603197)
//                });
//
//                sites.put("Грязевые вулканы Гобустана", new Site[]{new Site("Как добраться?", "Легко!", 37.0, 45.5)});
//                sites.put("Лучшие граффити Берлина", new Site[]{new Site("Берлинская стена", "Кусок стены монжно купить в киоске.", 37.0, 45.5)});
//
//                for (HashMap.Entry<String, Site[]> entry : sites.entrySet())
//                    sites.put(entry.getKey(), GeometryHelper.nearestInsertion(entry.getValue()));
//
//            }
//        });
    }

    public static class MapFragment extends Fragment  implements OnMapReadyCallback,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener, com.example.dmitry.naviguide.MapFragment {
        private int cur_site_index;

        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        LocationRequest mLocationRequest;
        private GoogleMap mMap;
        Marker[] markers;
        Polyline[] lines;

        public MapFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.map_fragment, container, false);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }
            SupportMapFragment mapFragment = (SupportMapFragment)
                    getChildFragmentManager()
                            .findFragmentById(R.id.map_on_fr);

            cur_site_index = 0;

            mapFragment.getMapAsync(this);


            return view;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            if (mGoogleApiClient == null) {
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(getActivity(), "permission denied",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.2f));
            if (RoutesSingletone.getInstance().getSites().get(((RouteActivity)getActivity()).route.name).length == cur_site_index + 1)
                return;
            float res[] = new float[2];
            LatLng ll = RoutesSingletone.getInstance().getSites().get(((RouteActivity)getActivity()).route.name)[cur_site_index + 1].getLatLng();
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), ll.latitude, ll.longitude, res);
            if (res[0] < 100) {
                ++cur_site_index;
                markers[cur_site_index].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                lines[cur_site_index - 1].setColor(Color.GREEN);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            //Initialize Google Play Services
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }

            Site prev = null;
            markers = new Marker[RoutesSingletone.getInstance().getSites().get(((RouteActivity) getActivity()).route.name).length];
            lines = new Polyline[markers.length - 1];
            cur_site_index = 0;
            int i = 0;

            for (Site site : RoutesSingletone.getInstance().getSites().get(((RouteActivity) getActivity()).route.name)) {
                markers[i] = setMarker(site.name, site.name, site.getLatLng(),  cur_site_index == i ? 1 : 0);
                if (prev != null)
                    lines[i - 1] = mMap.addPolyline(new PolylineOptions()
                                .add(prev.getLatLng(), site.getLatLng())
                                .width(8)
                                .color(Color.BLUE));
                prev = site;
                ++i;
            }


            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.setLocationSource(new LocationSource() {
                        @Override
                        public void activate(OnLocationChangedListener onLocationChangedListener) {
                            Location new_loc = new Location("");
                            new_loc.setLatitude(latLng.latitude);
                            new_loc.setLongitude(latLng.longitude);
                            onLocationChangedListener.onLocationChanged(new_loc);
                            onLocationChanged(new_loc);
                        }

                        @Override
                        public void deactivate() {

                        }
                    });
                }
            });

        }
        private Marker setMarker(String title, String subTitle, LatLng latLng, int type) {
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.title(title).snippet(subTitle);
            markerOpt.position(latLng);
            markerOpt.icon(type == 0 ? BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) :
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(getActivity());
            mMap.setInfoWindowAdapter(adapter);
            return mMap.addMarker(markerOpt);
        }


        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

        public boolean checkLocationPermission() {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        }
    }


    public static class SitesFragment extends Fragment {
        public SitesFragment() {

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.sites_fragment, container, false);

            SuperRecyclerView recycleView = view.findViewById(R.id.super_recycler);
            recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycleView.setAdapter(new SuperAdapter(getActivity(), ((RouteActivity) getActivity()).route.name));

            return view;
        }

    }

    public static class DescrFragment extends Fragment {
        public DescrFragment() {

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.descr_fragment, container, false);
            ((TextView)view.findViewById(R.id.descr_text)).setText(((RouteActivity)getActivity()).route.descr);
            ((TextView)view.findViewById(R.id.route_name)).setText(String.format("\n%s\n", ((RouteActivity)getActivity()).route.name));

            ImageView image = view.findViewById(R.id.route_image);
            int id = getActivity().getResources().getIdentifier(((RouteActivity)getActivity()).route.picture, "drawable", getActivity().getPackageName());
            image.setImageResource(id);

            return view;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return position == 1 ? sitesFragment : position == 2 ? mapFragment : descrFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Описание";
                case 1:
                    return "Места";
                case 2:
                    return "Карта";
            }
            return null;
        }

    }
}
