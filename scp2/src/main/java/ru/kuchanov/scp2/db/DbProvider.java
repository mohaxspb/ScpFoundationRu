package ru.kuchanov.scp2.db;

import java.util.Collection;

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
}