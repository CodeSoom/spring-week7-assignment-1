package com.codesoom.assignment.convertors;

/**
 * 클라이언트에 제공할 View DTO를 제공한다.
 *
 * @param <S> 변환 될 도메인 타입
 * @param <R> 변환 할 View DTO 타입
 */
@FunctionalInterface
public interface ViewSupplier<S, R> {

    /**
     * View DTO로 변환한다.
     *
     * @param source 변환 될 도메인
     * @return View DTO
     */
    R supply(S source);
}
