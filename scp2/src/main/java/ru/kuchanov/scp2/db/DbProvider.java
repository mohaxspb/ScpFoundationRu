package ru.kuchanov.scp2.db;

import android.util.Pair;

import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.kuchanov.scp2.db.model.Article;
import rx.Observable;

/**
 * Created by y.kuchanov on 21.12.16.
 * <p>
 * for TappAwards
 */
public class DbProvider {
    private Realm mRealm;

    public DbProvider() {
        mRealm = Realm.getDefaultInstance();
    }

    public <E extends RealmModel> E get(Class<E> clazz) {
        return mRealm.where(clazz).findFirst();
    }

    public <E extends RealmModel> E getAsync(Class<E> clazz) {
        return mRealm.where(clazz).findFirstAsync();
    }

    public <E extends RealmModel> Collection<E> getRowsAsync(Class<E> clazz) {
        return mRealm.where(clazz).findAllAsync();
    }

    public <E extends RealmModel> Collection<E> getRowsSortedAsync(Class<E> clazz, String fieldName, Sort sort) {
        return mRealm.where(clazz).findAllSortedAsync(fieldName, sort);
    }

    public void close() {
        mRealm.close();
    }

    public void deleteAll() {
        mRealm.executeTransaction(realm -> realm.deleteAll());
    }

    public <V extends RealmObject> Observable<V> getRowObservable(Class<V> clazz) {
        return mRealm.where(clazz).findFirst().<V>asObservable()
                .filter(realmObject -> realmObject.isLoaded())
                .filter(realmObject -> realmObject.isValid())
                .first();
    }

    public <V extends RealmObject> Observable<V> getRowObservableAsync(Class<V> clazz) {
        return mRealm.where(clazz).findFirstAsync().<V>asObservable()
                .filter(realmObject -> realmObject.isLoaded())
                .filter(realmObject -> realmObject.isValid())
                .first();
    }

    public <V extends RealmObject> Observable<RealmResults<V>> getRowsObservable(Class<V> clazz) {
        return mRealm.where(clazz).findAll().<RealmResults<V>>asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid);
    }

    public <V extends RealmObject> Observable<RealmResults<V>> getRowsObservableAsync(Class<V> clazz) {
        return mRealm.where(clazz).findAllAsync().<RealmResults<V>>asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid);
    }

    public Observable<RealmResults<Article>> getRecentArticlesSortedAsync(String field, Sort order) {
        return mRealm.where(Article.class)
                .notEqualTo(Article.FIELD_IS_IN_RECENT, Article.ORDER_NONE)
                .findAllSortedAsync(field, order)
                .asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid);
    }

    public Observable<RealmResults<Article>> getRatedArticlesSortedAsync(String field, Sort order) {
        return mRealm.where(Article.class)
                .notEqualTo(Article.FIELD_IS_IN_MOST_RATED, Article.ORDER_NONE)
                .findAllSortedAsync(field, order)
                .asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid);
    }

    public Observable<Pair<Integer, Integer>> saveRecentArticlesList(List<Article> apiData, int offset) {
        return Observable.create(subscriber -> {
            mRealm.executeTransactionAsync(
                    realm -> {
                        //remove all aps from nominees if we update list
                        if (offset == 0) {
                            List<Article> nomineesApps =
                                    realm.where(Article.class)
                                            .notEqualTo(Article.FIELD_IS_IN_RECENT, Article.ORDER_NONE)
                                            .findAll();
                            for (Article application : nomineesApps) {
                                application.isInRecent = Article.ORDER_NONE;
                            }
                        }
                        //check if we have app in db and update
                        for (int i = 0; i < apiData.size(); i++) {
                            Article applicationToWrite = apiData.get(i);
                            Article applicationInDb = realm.where(Article.class)
                                    .equalTo(Article.FIELD_URL, applicationToWrite.url)
                                    .findFirst();
                            if (applicationInDb != null) {
                                applicationInDb.isInRecent = offset + i;
                                applicationInDb.title = applicationToWrite.title;

                                applicationInDb.rating = applicationToWrite.rating;

                                applicationInDb.authorName = applicationToWrite.authorName;
                                applicationInDb.authorUrl = applicationToWrite.authorUrl;

                                applicationInDb.createdDate = applicationToWrite.createdDate;
                                applicationInDb.updatedDate = applicationToWrite.updatedDate;
                            } else {
                                applicationToWrite.isInRecent = offset + i;
                                realm.insertOrUpdate(applicationToWrite);
                            }
                        }
                    },
                    () -> {
                        subscriber.onNext(new Pair<>(apiData.size(), offset));
                        subscriber.onCompleted();
                        mRealm.close();
                    },
                    error -> {
                        subscriber.onError(error);
                        mRealm.close();
                    });
        });
    }

    public Observable<Pair<Integer, Integer>> saveRatedArticlesList(List<Article> data, int offset) {
        return Observable.create(subscriber -> {
            mRealm.executeTransactionAsync(
                    realm -> {
                        //remove all aps from nominees if we update list
                        if (offset == 0) {
                            List<Article> articleList =
                                    realm.where(Article.class)
                                            .notEqualTo(Article.FIELD_IS_IN_MOST_RATED, Article.ORDER_NONE)
                                            .findAll();
                            for (Article application : articleList) {
                                application.isInMostRated = Article.ORDER_NONE;
                            }
                        }
                        //check if we have app in db and update
                        for (int i = 0; i < data.size(); i++) {
                            Article applicationToWrite = data.get(i);
                            Article applicationInDb = realm.where(Article.class)
                                    .equalTo(Article.FIELD_URL, applicationToWrite.url)
                                    .findFirst();
                            if (applicationInDb != null) {
                                applicationInDb.isInMostRated = offset + i;
                                applicationInDb.title = applicationToWrite.title;

                                applicationInDb.rating = applicationToWrite.rating;
                            } else {
                                applicationToWrite.isInMostRated = offset + i;
                                realm.insertOrUpdate(applicationToWrite);
                            }
                        }
                    },
                    () -> {
                        subscriber.onNext(new Pair<>(data.size(), offset));
                        subscriber.onCompleted();
                        mRealm.close();
                    },
                    error -> {
                        subscriber.onError(error);
                        mRealm.close();
                    });
        });
    }

    public Observable<Article> getArticleAsync(String articleUrl) {
        return mRealm.where(Article.class)
                .equalTo(Article.FIELD_URL, articleUrl)
                .findAllAsync()
                .<List<Article>>asObservable()
                .filter(RealmResults::isLoaded)
                .filter(RealmResults::isValid)
                .flatMap(arts -> arts.isEmpty() ? Observable.just(null) : Observable.just(arts.first()));
    }

    public Observable<Void> saveArticle(Article article) {
        return Observable.create(subscriber -> {
            mRealm.executeTransactionAsync(
                    realm -> {
                        //check if we have app in db and update
                        Article applicationInDb = realm.where(Article.class)
                                .equalTo(Article.FIELD_URL, article.url)
                                .findFirst();
                        if (applicationInDb != null) {
                            applicationInDb.text = article.text;
                        } else {
                            realm.insertOrUpdate(article);
                        }
                    },
                    () -> {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                        mRealm.close();
                    },
                    error -> {
                        subscriber.onError(error);
                        mRealm.close();
                    });
        });
    }
}