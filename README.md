## ブログリーダー
___

アプリのダウンロードは[こちら](https://play.google.com/store/apps/details?id=com.kumaydevelop.blogreader)から

ブログを登録して、このアプリから更新確認・ブログ閲覧ができるRSSリーダーアプリです

#### 使用環境
Kotlin 1.2.30  
Android Studio 3.1  
Realm 2.1.1  

#### 使用技術
*データの登録・更新・削除*  
*RecyclerViewの使用*  
*RxJava + Retrofit2 + simplexmlを使った非同期通信*  
*okHttp3 + Jsoupを使った非同期処理*  
*JobServiceを使ったバックグラウンド処理とNotificationManagerを使った通知機能*  
*Junit4とEspresso3.0.2(UIテスト)を使った単体テスト*  

#### 画面イメージ

①URLを入力して、RSSのURLが存在しているかを確認、あれば、ブログを登録する。  
  一覧でブログ名を押すと、記事の一覧が表示されるので、見たい記事を押すと、記事が表示される。  

![rss1](https://user-images.githubusercontent.com/20049397/61211617-0167ce00-a73b-11e9-9737-9537f5652c49.gif)

②入力エラーとRSSのURLが存在しない場合のハンドリング  
  
![rss2](https://user-images.githubusercontent.com/20049397/61211654-20666000-a73b-11e9-8ce5-1f7462d23dca.gif)

③表示件数と更新確認のタイミングは設定から変更できる  

![rss3](https://user-images.githubusercontent.com/20049397/61211717-4f7cd180-a73b-11e9-9a29-dbf7db92047b.gif)

④通知を押すと、アプリが起動し、一覧の最終更新日時が最新化される。  
　記事の一覧にも最新のデータが表示される。  

![rss4](https://user-images.githubusercontent.com/20049397/61211756-63283800-a73b-11e9-91f7-bda49e88643f.gif)

⑤記事一覧でブログのセルを長押しすると、削除できる。  
 
![rss5](https://user-images.githubusercontent.com/20049397/61211799-7c30e900-a73b-11e9-88dd-9d732b2d682c.gif)
